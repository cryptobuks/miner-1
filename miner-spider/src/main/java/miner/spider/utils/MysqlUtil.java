package miner.spider.utils;

import com.mysql.jdbc.Driver;
import miner.spider.pojo.Data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Mysql utils
 */
public class MysqlUtil {

    // 返回mysql连接
    public static Connection getConnection() {
        String url = "jdbc:mysql://"+StaticValue.mysql_host+":"+StaticValue.mysql_port+"/"+StaticValue.mysql_database+"?useUnicode=true&characterEncoding=utf8";
        Properties info = new Properties();
        info.put("user", StaticValue.mysql_user);
        info.put("password", StaticValue.mysql_password);
        Driver driver;
        Connection con = null;
        try {
            driver = new Driver();
            con = driver.connect(url, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }

    public static List getProject(String projectName){
        List reList = new ArrayList();
        try {
            String wid = projectName.split("-")[0];
            String pid = projectName.split("-")[1];

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from project where wid="
                            + wid+" AND pid=" + pid);
            rs.next();// 指向有效的一行
            for(int i = 2; i < 9; i++){
                String re = rs.getString(i);
                reList.add(re);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return reList;
    }

    public static List getTask(String taskName){
        List reList = new ArrayList();
        try {
            String wid = taskName.split("-")[0];
            String pid = taskName.split("-")[1];
            String tid = taskName.split("-")[2];

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from task where wid="
                            + wid+" AND pid=" + pid+" AND tid=" + tid);
            rs.next();// 指向有效的一行
            for(int i = 2; i < 10; i++){
                String re = rs.getString(i);
                reList.add(re);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return reList;
    }


    public static List getTaskByProject(String wid, String pid){
        List reList = new ArrayList();
        try {
            String twid = wid;
            String tpid = pid;

            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from task where wid="
                            + wid+" AND pid=" + pid);
            while(rs.next()){
                String tid = rs.getString(4);
                reList.add(tid);
            }
        }catch(Exception ex){
            ex.printStackTrace();

        }

        return reList;
    }

    public static HashMap<String, Data> getData(){
        HashMap<String, Data> reData = new HashMap<String, Data>();
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from data");
            while(rs.next()){
                Data dt = new Data();
                dt.setWid(rs.getString("wid"));
                dt.setPid(rs.getString("pid"));
                dt.setTid(rs.getString("tid"));
                dt.setDid(rs.getString("dataid"));
                dt.setDesc(rs.getString("description"));
                dt.setProperty(rs.getString("property"));
                dt.setRowKey(rs.getString("rowKey"));
                dt.setForeignKey(rs.getString("foreignKey"));
                dt.setForeignValue(rs.getString("foreignValue"));
                dt.setLink(rs.getString("link"));
                dt.setProcessWay(rs.getString("processWay"));
                dt.setDocType(rs.getString("docType"));
                String hashKey = dt.getWid()+"-"+dt.getPid()+"-"+dt.getTid()+"-"+dt.getDid();
                System.out.println("hashKey:"+hashKey);
                System.out.println("property:"+dt.getProperty());
                reData.put(hashKey, dt);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return reData;
    }

    public static HashMap<String, String> getData(String dataName){
        String wid = dataName.split("-")[0];
        String pid = dataName.split("-")[1];
        String tid = dataName.split("-")[2];
        String did = dataName.split("-")[3];
        HashMap<String, String> reData = new HashMap<String, String>();
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from data where wid="+wid+" AND pid="+pid+" AND tid="+tid+" AND dataid="+did);
            rs.next();// 指向有效的一行

            reData.put("wid", wid);
            reData.put("pid", pid);
            reData.put("tid", tid);
            reData.put("did", did);

            reData.put("desc", rs.getString("description"));
            reData.put("property", rs.getString("property"));

            reData.put("rowKey", rs.getString("rowKey"));
            reData.put("foreignKey", rs.getString("foreignKey"));
            reData.put("foreignValue", rs.getString("foreignValue"));
            reData.put("link", rs.getString("link"));

            reData.put("processWay", rs.getString("processWay"));
            reData.put("docType", rs.getString("docType"));
//            System.out.println("property:"+reData.get("docType"));

        }catch(Exception ex){
            ex.printStackTrace();
        }

        return reData;
    }

    public static HashMap<String, String> getRegex(){
        HashMap<String, String> reRegex = new HashMap<String, String>();
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt
                    .executeQuery("select * from regex");
            while(rs.next()){
                String wid = rs.getString("wid");
                String pid = rs.getString("pid");
                String tid = rs.getString("tid");
                String tagName = rs.getString("tagname");
                String path = rs.getString("path");

                String hashKey = wid+"-"+pid+"-"+tid+"-"+tagName;
                System.out.println("hashKey:"+hashKey);
                System.out.println("property:" + path);
                reRegex.put(hashKey, path);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return reRegex;
    }

    public static void main(String[] args){
//        Connection conn = getConnection();
//        System.out.println(conn);
//        getProject("1-1");
//        getTask("1-1-1");
//        getData();
//        getRegex();
        getData("1-1-1-1");
    }

}
