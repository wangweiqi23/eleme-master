package com.wwq.eleme.mode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vin on 08/12/2016.
 */

public class Goods {

    private int cId;
    private String cName;
    private List<SubModel> subModelList;


    private int badge;

    public Goods(int cId, String cName, List<SubModel> subModelList) {
        this.cId = cId;
        this.cName = cName;
        this.subModelList = subModelList;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public List<SubModel> getSubModelList() {
        return subModelList;
    }

    public void setSubModelList(List<SubModel> subModelList) {
        this.subModelList = subModelList;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

    public static class SubModel {

        /*****tag*****/
        private int cId;
        private String cName;
        /*************/

        private int num;

        private String name;
        private int age;

        public int getcId() {
            return cId;
        }

        public void setcId(int cId) {
            this.cId = cId;
        }

        public String getcName() {
            return cName;
        }

        public void setcName(String cName) {
            this.cName = cName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public SubModel(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }
    }


    public static List<Goods> initData(){

        List<Goods> list = new ArrayList<>();

        List<SubModel> hot = new ArrayList<>();
        hot.add(new SubModel("全家桶",1));
        hot.add(new SubModel("全翅桶",3));
        hot.add(new SubModel("小食四拼",4));

        List<SubModel> discount = new ArrayList<>();
        discount.add(new SubModel("满100元减20元",1));
        discount.add(new SubModel("满50元减10元",3));
        discount.add(new SubModel("无门槛3元",4));

        List<SubModel> food = new ArrayList<>();
        food.add(new SubModel("奥尔良鸡腿汉堡",20));
        food.add(new SubModel("鳕鱼堡",18));
        food.add(new SubModel("鸡腿",22));
        food.add(new SubModel("草莓派",60));
        food.add(new SubModel("饭",20));
        food.add(new SubModel("烤全翅",10));
        food.add(new SubModel("粥",23));
        food.add(new SubModel("油条",25));

        List<SubModel> drink = new ArrayList<>();
        drink.add(new SubModel("可乐",10));
        drink.add(new SubModel("雪碧",18));
        drink.add(new SubModel("芬达",21));
        drink.add(new SubModel("奶茶",21));
        drink.add(new SubModel("咖啡",11));
        drink.add(new SubModel("豆浆",18));
        drink.add(new SubModel("白开水",58));

        List<SubModel> other = new ArrayList<>();
        other.add(new SubModel("全家桶",1));
        other.add(new SubModel("8人拼小食",2));
        other.add(new SubModel("全翅桶",3));
        other.add(new SubModel("小食四拼",4));


        list.add(new Goods(0,"热销",hot));
        list.add(new Goods(1,"优惠",discount));
        list.add(new Goods(2,"套餐",other));
        list.add(new Goods(3,"主食",food));
        list.add(new Goods(4,"饮料",drink));
        list.add(new Goods(5,"套餐A",other));
        list.add(new Goods(6,"套餐B",other));
        list.add(new Goods(7,"套餐C",other));
        list.add(new Goods(8,"套餐D",other));
        list.add(new Goods(9,"套餐E",other));
        list.add(new Goods(10,"套餐F",other));
        list.add(new Goods(11,"套餐G",other));
        list.add(new Goods(12,"套餐H",other));


        return list;
    }
}
