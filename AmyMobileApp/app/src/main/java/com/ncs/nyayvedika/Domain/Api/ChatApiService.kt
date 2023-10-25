package com.ncs.nyayvedika.Domain.Api

import com.ncs.nyayvedika.Domain.Models.Answer
import com.ncs.nyayvedika.Domain.Models.Question
import com.ncs.nyayvedika.Domain.Models.TestingToken
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/*
File : VedikaApi.kt -> com.ncs.nyayvedika.Domain.Interfaces
Description : Api Interface for Vedika Backend 

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 11:52 pm on 17/09/23

Todo >
Tasks CLEAN CODE : 
Tasks BUG FIXES : 
Tasks FEATURE MUST HAVE : 
Tasks FUTURE ADDITION : 

*/

interface ChatApiService {
    @POST("/query")
    @Headers("Content-Type: application/json")
    suspend fun getAnswer(@Body question: Question) : Response<Answer>

    //Created by T.U
    @GET("")
    suspend fun getToken( ):Response<TestingToken>

}