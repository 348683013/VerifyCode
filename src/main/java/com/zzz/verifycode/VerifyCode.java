package com.zzz.verifycode;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import java.util.UUID;

/**
 * 生成验证码类
 * author:zhouzhongzhong
 * date:2021/12/17,20:55
 */
public class VerifyCode {
    private static String VerifyCodeImgName;

    //读取验证码图片并输出验证码图片到前端页面，这个会自动创建一张验证码图片
    public static String outputVerifyCodeImgToHtml(String imgPath, HttpServletResponse response) {
        FileInputStream fis = null;
        response.setContentType("image/jpeg");
        String verifyCode;
        try {
            OutputStream out = response.getOutputStream();

            //验证码文件所在地址
            String path = imgPath;
            verifyCode = VerifyCode.createVerifyCode(path);//创建验证码图片到指定地址
            String pathAndName = path + "\\" + VerifyCode.getVerifyCodeImgName(); //验证码地址和验证码名字拼接

            File file = new File(pathAndName);
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e + "输出图片到html页面异常");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e + "输出图片到html页面异常_流关闭异常");
                }
            }
        }
        return verifyCode;
    }

    //生成验证码图片，并返回验证码字符串
    public static String createVerifyCode(String ImgPath) {
        BufferedImage bi = new BufferedImage(70, 30, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();//得到画笔
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 70, 30);//背景大小
        g.setColor(Color.red);
        g.setFont(new Font("Times New Roman", Font.ITALIC + Font.BOLD, 17)); //设置字体样式和大小和字体加粗
        String randomString = VerifyCode.getRandomString();

        //给每两个字符之间添加空格
        String[] split = randomString.split("");
        String randomString2 = " ";
        for (int i = 0; i < split.length; i++) {
            randomString2 += split[i] + "  ";
        }
        g.drawString(randomString2, 2, 21); //设置内容和位置

        //生成干扰线
        g.setColor(Color.gray);
        g.setStroke(new BasicStroke(1)); //设置粗细
        for (int i = 0; i < 6; i++) {
            int x = new Random().nextInt(70);
            int y = new Random().nextInt(30);
            int x1 = new Random().nextInt(70);
            int y1 = new Random().nextInt(30);
            g.drawLine(x, y, x + x1, y + y1);
        }

        //生成的验证码文件名
        String imgName = UUID.randomUUID().toString();

        try {
            ImageIO.write(bi, "JPEG", new FileOutputStream(ImgPath + "/" + imgName + ".jpg")); //图片保存路径和保存名称
        } catch (IOException e) {
            throw new RuntimeException("验证码生成失败！");
        }

        System.out.println("生成验证码：" + ImgPath + "/" + imgName + ".jpg" + "___" + randomString);
        //保存图片文件名
        setVerifyCodeImgName(imgName + ".jpg");

        //最后要返回生成的字符串
        return randomString;
    }

    //得到长度为4的字符串
    public static String getRandomString() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String substring = uuid.substring(0, 4);
        return substring;
    }

    //删除用完的验证码图片
    public static void deleteVerifyCodeImg(String imgPath, String verifyCodeImgName) {
        File imgFile = new File(imgPath + "/" + verifyCodeImgName);
        imgFile.delete();
    }

    //得到生成验证码图片的文件名
    public static String getVerifyCodeImgName() {
        return VerifyCodeImgName;
    }

    //保存生成验证码图片的文件名
    public static void setVerifyCodeImgName(String verifyCodeImgName) {
        VerifyCodeImgName = verifyCodeImgName;
    }
}
