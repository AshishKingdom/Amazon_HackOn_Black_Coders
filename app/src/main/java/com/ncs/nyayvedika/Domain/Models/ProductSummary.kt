package com.ncs.nyayvedika.Domain.Models

/*
File : ProductSummary -> com.ncs.nyayvedika.Domain.Models
Description : Product Summary 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 4:15 am on 25/10/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 


*/data class ProductSummary(
    val productTitle: String,
    val productPriceNow: String,
    val productPriceEarlier: String,
    val productRatings: Int,
    val productRatingCount: Int,
    val productLink: String,
    val imageLink: String
)
