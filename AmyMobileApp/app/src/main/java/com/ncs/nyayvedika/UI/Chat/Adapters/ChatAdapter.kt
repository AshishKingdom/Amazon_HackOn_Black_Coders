package com.ncs.nyayvedika.UI.Chat.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.ncs.nyayvedika.Domain.Models.PdfMessage
import com.ncs.nyayvedika.Domain.Models.ProductSummary
import com.ncs.nyayvedika.R
import com.ncs.nyayvedika.databinding.BotCommentItemBinding
import com.ncs.nyayvedika.databinding.BotMessageItemBinding
import com.ncs.nyayvedika.databinding.BotMessagePdfItemBinding
import com.ncs.nyayvedika.databinding.BotMessageTypingItemBinding
import com.ncs.nyayvedika.databinding.UserMessageItemBinding
import com.ncs.o2.Domain.Models.Message
import com.ncs.o2.Domain.Utility.ExtensionsUtil.gone
import com.ncs.o2.Domain.Utility.ExtensionsUtil.isNull
import com.ncs.o2.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.o2.Domain.Utility.ExtensionsUtil.visible
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
        const val MESSAGE_TYPE_BOT_COMMENT = 4
    }


    private inner class BotMessage_ViewHolder( val binding : BotMessageItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int){

            val msg = msgList.get(position).message
            //binding.tvMessage.setText(msg)

            //val productID= extractNumber(msg)?.toInt()
            val productID= 782
            Toast.makeText(context, productID.toString(), Toast.LENGTH_SHORT).show()

            if (productID!=null){
                val ps = getProductFromCsv(productID)
                if (ps==null){
                    binding.productSummary.gone()
                    markwon.setMarkdown(binding.tvMessage, "\"Sorry, no matching products :(\"")
                }
                else {

                    //Title
                    binding.productTitle.text = ps.productTitle

                    //Product image
                    Glide.with(context)
                        .load(ps.imageLink)
                        .placeholder(R.drawable.amzn)
                        .into(binding.productImage)

                    //New price
                    ps.productPriceNowString.also { binding.productPrizeNow.text = it }
                    ps.productPriceEarlierString.also { binding.productPrizeEarlier.text = it }

                    //Percent discount
                    val priceEarlier = ps.productPriceEarlierInt
                    val priceNow = ps.productPriceNowInt
                    val diff = priceEarlier - priceNow
                    val div: Float = diff/priceEarlier.toFloat()
                    val discount = div * 100

                    binding.productPrizeOffPercent.text = "${discount.toInt()}%"

                    //Rating
                    binding.ratingBar1.rating = ps.productRatings
                    binding.productRatingNum.text = ps.productRatingCount.toString()

                    markwon.setMarkdown(binding.tvMessage, ps.productTitle)
                    binding.productSummary.visible()

                    binding.productSummary.setOnClickThrottleBounceListener{
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(ps.productLink)
                        context.startActivity(intent)
                    }

                }

            }else {
                binding.productSummary.gone()
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


    private inner class BotMessage_Comment_ViewHolder( val binding : BotCommentItemBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(position: Int){

            val msg : Message = msgList[position]
            binding.tvMessage.text = msg.message

            if (msg.message.equals("Hey there \uD83D\uDC4B\uD83C\uDFFB")){
                binding.profileHeader.visible()
            }else {
                binding.profileHeader.gone()
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

            MESSAGE_TYPE_BOT_COMMENT -> {
                BotMessage_Comment_ViewHolder(BotCommentItemBinding.inflate(LayoutInflater.from(context),parent,false))
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
        else if (msgList[position].msgType == MESSAGE_TYPE_BOT_COMMENT) {
            (holder as BotMessage_Comment_ViewHolder).bind(position)
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



    fun getProductFromCsv(productId : Int): ProductSummary? {
        val inputStream = context.resources.openRawResource(R.raw.data)
        var productSummary : ProductSummary? = null
        csvReader().open(inputStream) {

            val productSequence = readAllWithHeaderAsSequence()
            for (field in productSequence) {
                val currentProductId = field["product_id"]?.toInt()
                if (currentProductId == productId) {

                    productSummary =  ProductSummary(
                        productID = currentProductId,
                        productTitle = field["name"].toString(),
                        productPriceNowInt = field["discount_price"].toString().replace(Regex("[₹,]"),"").toInt(),
                        productPriceNowString = field["discount_price"].toString(),

                        productPriceEarlierInt = field["actual_price"].toString().replace(Regex("[₹,]"),"").toInt(),
                        productPriceEarlierString = field["actual_price"].toString(),

                        productRatings = field["ratings"]!!.toFloat(),
                        productRatingCount = field["no_of_ratings"].toString().replace(",","").toInt(),
                        productLink = field["link"].toString(),
                        imageLink = field["image"].toString()
                    )

                }
            }
        }

        return productSummary
    }




}