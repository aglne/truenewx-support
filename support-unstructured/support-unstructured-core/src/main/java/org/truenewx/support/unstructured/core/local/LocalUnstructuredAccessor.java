package org.truenewx.support.unstructured.core.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.truenewx.core.Strings;
import org.truenewx.core.io.AttachInputStream;
import org.truenewx.core.io.AttachOutputStream;
import org.truenewx.core.io.CompositeOutputStream;
import org.truenewx.core.util.IOUtil;
import org.truenewx.core.util.StringUtil;
import org.truenewx.support.unstructured.core.UnstructuredAccessor;
import org.truenewx.support.unstructured.core.model.UnstructuredStorageMetadata;

/**
 * 本地的非结构化存储访问器
 *
 * @author jianglei
 *
 */
public class LocalUnstructuredAccessor implements UnstructuredAccessor {

    private File root;
    private UnstructuredAccessor remoteAccessor;
    private Executor executor;

    public LocalUnstructuredAccessor(final String root) {
        final File file = new File(root);
        if (!file.exists()) { // 目录不存在则创建
            file.mkdirs();
        } else { // 必须是个目录
            Assert.isTrue(file.isDirectory(), "root must be a directory");
        }
        Assert.isTrue(file.canRead() && file.canWrite(), "root can not read or write");

        this.root = file;
    }

    /**
     * @param remoteAccessor
     *            远程访问器
     */
    public void setRemoteAccessor(final UnstructuredAccessor remoteAccessor) {
        if (remoteAccessor != this) { // 避免自己引用自己从而导致无限递归调用
            this.remoteAccessor = remoteAccessor;
        }
    }

    /**
     * @param executor
     *            线程执行器
     */
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }

    @Override
    public void write(final String bucket, final String path, final String filename,
            final InputStream in) throws IOException {
        // 先上传内容到一个新建的临时文件中，以免在处理过程中原文件被读取
        final File tempFile = createTempFile(bucket, path);
        final OutputStream out = new AttachOutputStream(new FileOutputStream(tempFile), filename);
        IOUtil.writeAll(in, out); // 最后写入原始文件流
        out.close();

        // 然后删除原文件，修改临时文件名为原文件名
        final File file = getStorageFile(bucket, path);
        if (file.exists()) {
            file.delete();
        }
        tempFile.renameTo(file);

        // 写至远程服务器
        writeToRemote(bucket, path, filename, file);
    }

    private File createTempFile(final String bucket, final String path) throws IOException {
        // 形如：${正式文件名}_${32位UUID}.temp;
        final String relativePath = standardize(bucket) + standardize(path) + Strings.UNDERLINE
                + StringUtil.uuid32() + Strings.DOT + "temp";
        final File file = new File(this.root, relativePath);
        ensureDirs(file);
        file.createNewFile(); // 创建新文件以写入内容
        file.setWritable(true);
        return file;
    }

    /**
     * 确保指定文件的所属目录存在
     *
     * @param file
     *            文件
     */
    private void ensureDirs(final File file) {
        File parent = file.getParentFile();
        // 上级目录路径中可能已经存在一个同名文件，导致目录无法创建，此时修改该文件的名称
        while (parent != null) {
            if (parent.exists() && !parent.isDirectory()) {
                parent.renameTo(new File(parent.getAbsolutePath() + ".temp"));
                break;
            }
            parent = parent.getParentFile();
        }
        file.getParentFile().mkdirs(); // 确保目录存在
    }

    private File getStorageFile(final String bucket, final String path) {
        final String relativePath = standardize(bucket) + standardize(path);
        final File file = new File(this.root, relativePath);
        ensureDirs(file);
        return file;
    }

    private String standardize(String path) {
        // 必须以斜杠开头，不能以斜杠结尾
        if (!path.startsWith(Strings.SLASH)) {
            path = Strings.SLASH + path;
        }
        if (path.endsWith(Strings.SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void writeToRemote(final String bucket, final String path, final String filename,
            final File file) {
        if (this.executor != null && this.remoteAccessor != null) {
            this.executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 上传至远程的文件内容不包含附加信息
                        final InputStream in = new AttachInputStream(new FileInputStream(file));
                        LocalUnstructuredAccessor.this.remoteAccessor.write(bucket, path, filename,
                                in);
                        in.close();
                    } catch (final IOException e) {
                        LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                    }
                }
            });
        }
    }

    @Override
    public UnstructuredStorageMetadata getStorageMetadata(final String bucket, final String path) {
        try {
            final File file = getStorageFile(bucket, path);
            if (file.exists()) {
                final AttachInputStream in = new AttachInputStream(new FileInputStream(file));
                final String filename = in.readAttachement();
                in.close();
                return new UnstructuredStorageMetadata(filename, file.length(),
                        file.lastModified());
            } else if (this.remoteAccessor != null) {
                return this.remoteAccessor.getStorageMetadata(bucket, path);
            }
        } catch (final Exception e) {
            // 忽略所有异常
        }
        return null;
    }

    @Override
    public long getLastModifiedTime(final String bucket, final String path) {
        try {
            final File file = getStorageFile(bucket, path);
            if (file.exists()) {
                return file.lastModified();
            } else if (this.remoteAccessor != null) {
                return this.remoteAccessor.getLastModifiedTime(bucket, path);
            }
        } catch (final Exception e) {
            // 忽略所有异常
        }
        return 0;
    }

    @Override
    public void read(final String bucket, final String path, final OutputStream out)
            throws IOException {
        final File file = getStorageFile(bucket, path);
        if (!file.exists()) { // 如果文件不存在，则需要从远程服务器读取内容，并缓存到本地文件
            if (this.remoteAccessor != null) {
                final UnstructuredStorageMetadata metadata = this.remoteAccessor
                        .getStorageMetadata(bucket, path);
                if (metadata != null) {
                    file.createNewFile();
                    final String filename = metadata.getFilename();
                    final OutputStream fileOut = new AttachOutputStream(new FileOutputStream(file),
                            filename);
                    this.remoteAccessor.read(bucket, path, new CompositeOutputStream(out, fileOut));
                    fileOut.close();
                }
            }
        } else {
            final InputStream in = new AttachInputStream(new FileInputStream(file));
            IOUtil.writeAll(in, out);
            in.close();
        }
    }

}
