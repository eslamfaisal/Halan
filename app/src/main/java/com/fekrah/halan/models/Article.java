package com.fekrah.halan.models;

public class Article {
    private String article_text;
    private String article_key;
    private String article_Img;
    private String user_key;
    public Article() {
    }

    public Article(String article_text, String article_key, String article_Img) {
        this.article_text = article_text;
        this.article_key = article_key;
        this.article_Img = article_Img;
    }

    public Article(String article_text, String article_key, String article_Img, String user_key) {
        this.article_text = article_text;
        this.article_key = article_key;
        this.article_Img = article_Img;
        this.user_key = user_key;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getArticle_text() {
        return article_text;
    }

    public void setArticle_text(String article_text) {
        this.article_text = article_text;
    }

    public String getArticle_key() {
        return article_key;
    }

    public void setArticle_key(String article_key) {
        this.article_key = article_key;
    }

    public String getArticle_Img() {
        return article_Img;
    }

    public void setArticle_Img(String article_Img) {
        this.article_Img = article_Img;
    }
}
