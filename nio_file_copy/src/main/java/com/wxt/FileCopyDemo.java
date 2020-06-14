package com.wxt;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileCopyDemo {

    public static void close(Closeable closeable)
    {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        final FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileInputStream fin = null;
                FileOutputStream fout = null;

                try {
                    fin = new FileInputStream(source);
                    fout = new FileOutputStream(target);
                    int len;
                    while ((len=fin.read())!=-1)
                    {
                        fout.write(len);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "noBufferStreamCopy";
            }
        };

        FileCopyRunner bufferStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                BufferedInputStream bin = null;
                BufferedOutputStream bout = null;
                try {
                    bin = new BufferedInputStream(new FileInputStream(source));
                    bout = new BufferedOutputStream(new FileOutputStream(target));
                    byte[] buffer = new byte[1024];
                    int len  = 0;
                    while ((len=bin.read(buffer))!=-1){
                        bout.write(buffer,0,len);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "bufferStreamCopy";
            }
        };

        FileCopyRunner nioBufferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;
                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len=fin.read(byteBuffer))!=-1)
                    {
                        byteBuffer.flip();
                        while (byteBuffer.hasRemaining())
                        {
                            fout.write(byteBuffer);
                        }
                        byteBuffer.clear();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "copyFile";
            }
        };

        FileCopyRunner nioTransferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;
                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    Long len = 0L;
                    while (len!=fin.size())
                    {
                        len += fin.transferTo(0,fin.size(),fout);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e)
                {
                    e.printStackTrace();
                }finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "nioTransFerCopy";
            }
        };

        File soure = new File("G:\\JAVA\\a.log");
        File target = new File("G:\\JAVA\\aa.log");

        run(noBufferStreamCopy,soure,target );
        run(bufferStreamCopy,soure,target);
        run(nioBufferCopy,soure,target);
        run(nioTransferCopy,soure,target);

    }

    public static void run(FileCopyRunner test,File source, File target)
    {
        long sum = 0L;
        for (int i=0;i<5;i++)
        {
            long begin = System.currentTimeMillis();
            test.copyFile(source,target);
            long end = System.currentTimeMillis();
            sum += (end-begin);
        }
        System.out.println(test.toString()+ ": "+ sum);

    }

}
