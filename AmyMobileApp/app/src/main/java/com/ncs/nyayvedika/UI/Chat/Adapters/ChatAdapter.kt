package com.ncs.nyayvedika.UI.Chat.Adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ncs.nyayvedika.Domain.Models.PdfMessage
import com.ncs.nyayvedika.Domain.Models.ProductSummary
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.databinding.BotMessageItemBinding
import com.ncs.nyayvedika.databinding.BotMessagePdfItemBinding
import com.ncs.nyayvedika.databinding.BotMessageTypingItemBinding
import com.ncs.nyayvedika.databinding.UserMessageItemBinding
import com.ncs.o2.Domain.Models.Message
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import io.noties.markwon.Markwon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern


/*
File : ChatAdapter.kt -> com.ncs.nyayvedika.UI.Chat.Adapters
Description : Adapter for chats

Author : Alok Ranjan (VC uname : apple)
Link : https://github.com/arpitmx
From : Bitpolarity x Noshbae (@Project : NyayVedika Android)

Creation : 5:46 pm on 16/09/23

Todo >
Tasks CLEAN CODE :
Tasks BUG FIXES :
Tasks FEATURE MUST HAVE :
Tasks FUTURE ADDITION :

*/

class ChatAdapter(var msgList: MutableList<Message>, val context : Context, val markwon: Markwon, private val onClickCallback: OnClickCallback
, private val openPDFcallback:PdfHandlerCallback
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface onMessageClick {
        fun onClick(position: Int)
    }

    interface onRecentAppClick {
        fun onRecentClick(position: Int)
    }

    companion object {
        const val MESSAGE_TYPE_BOT = 0
        const val MESSAGE_TYPE_USER = 1
        const val MESSAGE_TYPE_BOT_PDF = 2
        const val MESSAGE_TYPE_BOT_TYPING = 3
    }


    private inner class BotMessage_ViewHolder( val binding : BotMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int){
            val msg = msgList.get(position).message
            //binding.tvMessage.setText(msg)

           // val productID= extractNumber(msg)
            val productID= 8291
            Toast.makeText(context, productID.toString(), Toast.LENGTH_SHORT).show()

            if (productID!=null){

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val inputStream = context.resources.openRawResource(R.raw.data)
                        val reader = BufferedReader(InputStreamReader(inputStream))

                        var pd: ProductSummary? = null

                        reader.useLines { lines ->
                            for (line in lines) {
                                val parts = line.split(",")
                                if (parts.size == 10) {

                                    val id = parts[9].toInt()

                                    if (id == productID.toInt()) {

                                        val name = parts[0]
                                        val image = parts[3]
                                        val link = parts[4]
                                        val ratings = parts[5].toInt()
                                        val noOfRatings = parts[6].toInt()
                                        val discountPrice = parts[7]
                                        val actualPrice = parts[8]
                                        pd = ProductSummary(productTitle= name, productPriceNow = discountPrice,
                                            productPriceEarlier= actualPrice, productRatings = noOfRatings,
                                            productRatingCount = ratings, productLink = link, imageLink = image )
                                        break
                                    }
                                }
                            }
                        }


                        withContext(Dispatchers.Main) {
                            if (pd != null) {

                                markwon.setMarkdown(binding.tvMessage,
                                    "${productID}\n${pd!!.productTitle}\n"
                                )



                               // Toast.makeText(context, pd!!.productPriceNow, Toast.LENGTH_SHORT).show()
                                //binding.productTitle.text = pd!!.productTitle

//                                Glide.with(context.applicationContext)
//                                    .load(pd!!.imageLink)
//                                    .into(binding.productImage)
                            } else {
                                Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
                            }


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


            }else {
                markwon.setMarkdown(binding.tvMessage, "\"Sorry, no matching products :(\"")
            }




            binding.btnCopy.setOnClickThrottleBounceListener {
                onClickCallback.copyClick(msg)
            }

            binding.tvMessage.setOnLongClickListener{
                val productID= extractNumber(msg)
                val msgObj = Message(msg, MESSAGE_TYPE_BOT)
                onClickCallback.onLongClick(msgObj)
                true
            }
        }
    }



    private inner class BotMessage_PDF_ViewHolder( val binding : BotMessagePdfItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int){

            val msg : PdfMessage = msgList.get(position) as PdfMessage
            binding.pdfName.text = msg.fileName
            binding.pdfThumb.setImageBitmap(msg.bitmap)
            binding.btnOpenPdf.setOnClickThrottleBounceListener(600){
                openPDFcallback.openPdf(msg.uri)
            }

            binding.btnSendPdf.setOnClickThrottleBounceListener(600){
                openPDFcallback.sendPdf(msg.uri)
            }
        }

    }


    fun extractNumber(inputString: String): String? {
        val numberPattern = Pattern.compile("\\d+")
        val numberMatcher = numberPattern.matcher(inputString)

        if (numberMatcher.find()) {
            return numberMatcher.group()
        }

        return null
    }

    private inner class BotMessage_Typing_ViewHolder( val binding : BotMessageTypingItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int){
        }

    }


    private inner class UserMessage_ViewHolder( val binding: UserMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            val msg = msgList.get(position).message
            binding.tvMessage.setText(msg)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_BOT -> {
                BotMessage_ViewHolder(BotMessageItemBinding.inflate(LayoutInflater.from(context),parent,false))

            }
            MESSAGE_TYPE_USER -> {
                UserMessage_ViewHolder(UserMessageItemBinding.inflate(LayoutInflater.from(context),parent,false))
            }

            MESSAGE_TYPE_BOT_PDF -> {
                BotMessage_PDF_ViewHolder(BotMessagePdfItemBinding.inflate(LayoutInflater.from(context),parent,false))
            }

            MESSAGE_TYPE_BOT_TYPING -> {
                BotMessage_Typing_ViewHolder(BotMessageTypingItemBinding.inflate(LayoutInflater.from(context),parent,false))
            }

            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (msgList[position].msgType == MESSAGE_TYPE_BOT) {
            (holder as BotMessage_ViewHolder).bind(position)

        } else if (msgList[position].msgType == MESSAGE_TYPE_USER) {
            (holder as UserMessage_ViewHolder).bind(position)
        }
        else if (msgList[position].msgType == MESSAGE_TYPE_BOT_PDF) {
            (holder as BotMessage_PDF_ViewHolder).bind(position)
        }
        else if (msgList[position].msgType == MESSAGE_TYPE_BOT_TYPING) {
            (holder as BotMessage_Typing_ViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return msgList[position].msgType
    }

    public fun appendMessage(msg:Message){
        msgList.add(msg)
        notifyDataSetChanged()
    }

    public fun showTyping(show:Boolean){
       if (show){
           msgList.add(Message("Typing",3))
           notifyDataSetChanged()
       }else{
           msgList.removeAt(msgList.size-1)
           notifyDataSetChanged()
       }
    }



    interface OnClickCallback{
        fun onLongClick(msg: Message)
        fun copyClick(msg:String)
    }

    interface PdfHandlerCallback{
        fun openPdf(uri: Uri)
        fun sendPdf(uri:Uri)
    }



//    override fun onClicking(position: Int) {
//        Toast.makeText(context, "app from list is clicked", Toast.LENGTH_SHORT).show()
//        //TODO App advertise list functionality add
//    }


    private fun searchProductById(context: Context, productId: Int) {

    }




}