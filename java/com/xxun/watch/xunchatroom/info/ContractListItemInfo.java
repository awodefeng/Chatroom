package com.xxun.watch.xunchatroom.info;

public class ContractListItemInfo {

    private String mGID;
    private String nickName;
    private int miss_count;
    private int attr;
    private String avatar;

    public ContractListItemInfo( String GID,String name,int count,int attr)
    {

        this.mGID=GID;
        this.nickName=name;
        this.miss_count=count;
        this.attr=attr;

    }

    public ContractListItemInfo()
    {

    }


    public String getContractItemGID(){
        return this.mGID;
    }

    public void setContractItemGID(String GID){
        this.mGID=GID;
    }

    public String getContractItemName(){
        return this.nickName;
    }

    public void setListItemName(String name){
        this.nickName=name;
    }

    public  int getContractItemMissCount(){
        return this.miss_count;
    }

    public void setContractItemMissCount(int count){
        this.miss_count=count;
    }

    public  void setContractItemAttr(int attr){
       this.attr= attr;
    }

    public  int getContractItemAttr(){
        return this.attr;
    }

    public  void setContractItemAvatar(String avatar){
       this.avatar= avatar;
    }

    public  String getContractItemAvatar(){
        return this.avatar;
    }
}
