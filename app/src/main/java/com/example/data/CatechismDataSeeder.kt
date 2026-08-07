package com.example.data

object CatechismDataSeeder {

    fun getInitialCatechismArticles(): List<CatechismArticle> {
        val list = mutableListOf<CatechismArticle>()

        list.addAll(CatechismPart1.getArticles()) // Q1 - Q90
        list.addAll(CatechismPart2.getArticles()) // Q91 - Q180
        list.addAll(CatechismPart3.getArticles()) // Q181 - Q270
        list.addAll(CatechismPart4.getArticles()) // Q271 - Q370

        return list
    }
}
