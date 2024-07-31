package com.cute.chat.viewmodel

import android.os.Message
import com.cute.im.IMCore
import com.cute.im.bean.MessageStatus
import com.cute.im.bean.Msg
import com.cute.im.service.ConversationService
import com.cute.im.service.MessageService
import com.cute.logic.http.model.MessageRepository
import com.cute.logic.http.model.UploadRepository
import com.cute.logic.http.response.tool.UploadResult
import com.cute.message.custom.msg.BlurImageMessage
import com.cute.message.custom.msg.BlurVideoMessage
import com.cute.message.custom.msg.ImageMessage
import com.cute.message.custom.msg.TextMessage
import com.cute.message.custom.msg.VideoMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

object MessageGlobalViewModel {


    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _messageRepository = MessageRepository()

    private val _repository = IMCore.getService(MessageService::class.java)


    fun unlockBlurImageMessage(
        message: Msg,
        imageMessage: BlurImageMessage,
        resultCallback: ((msg: Msg) -> Unit)?
    ) {
        viewModelScope.launch {
            val response =
                _messageRepository.unlockBlurMessage(message.messageId, imageMessage.url!!, false)
            if (response.isSuccess) {
                imageMessage.isBlur = false
                message.message = imageMessage
                _repository.updateMessage(message, true)
            }
            resultCallback?.invoke(message)
        }
    }

    fun unlockBlurVideoMessage(
        message: Msg,
        videoMessage: BlurVideoMessage,
        resultCallback: ((msg: Msg) -> Unit)?
    ) {
        viewModelScope.launch {
            val response =
                _messageRepository.unlockBlurMessage(message.messageId, videoMessage.url!!, true)
            if (response.isSuccess) {
                videoMessage.isBlur = false
                message.message = videoMessage
                _repository.updateMessage(message, true)
            }
            resultCallback?.invoke(message)
        }
    }

    fun sendTextMessage(
        uid: Long,
        peerId: Long, message: String, resultCallback: ((msg: Msg) -> Unit)?
    ) {
        viewModelScope.launch {
            val textMessage = TextMessage()
            textMessage.messageContent = message
            val textImMessage =
                _repository.generateIMMessage("$uid", "$peerId", textMessage)
            _repository.insertMessage(textImMessage)
            sendMessage(textImMessage, resultCallback = resultCallback)
        }
    }


    fun sendVideoMessage(
        uid: Long,
        peerId: Long, video: File, resultCallback: ((msg: Msg) -> Unit)?
    ) {
        viewModelScope.launch {
            val videoMessage = VideoMessage()
            videoMessage.url = video.path
            val videoImMessage =
                _repository.generateIMMessage("$uid", "$peerId", videoMessage)
            _repository.insertMessage(videoImMessage)
            uploadVideo(videoImMessage, video, resultCallback)
        }
    }

    fun sendImageMessage(
        uid: Long,
        peerId: Long, picture: File, resultCallback: ((msg: Msg) -> Unit)?
    ) {
        viewModelScope.launch {
            val imageMessage = ImageMessage()
            imageMessage.url = picture.path
            val imageImMessage =
                _repository.generateIMMessage("$uid", "$peerId", imageMessage)
            _repository.insertMessage(imageImMessage)
            uploadImage(imageImMessage, picture, resultCallback)
        }
    }

    private suspend fun uploadImage(msg: Msg, file: File, resultCallback: ((msg: Msg) -> Unit)?) {
        val result =
            UploadRepository.uploadPicture(file, "${System.currentTimeMillis()}.${file.extension}")
        if (result is UploadResult.Success) {
            if (msg.message is ImageMessage) {
                (msg.message as ImageMessage).url = result.fileUrl
            }
            IMCore.getService(MessageService::class.java).updateMessage(msg)
            sendMessage(msg, resultCallback = resultCallback)
        } else {
            updateMsgStatus(msg, MessageStatus.FAIL)
            resultCallback?.invoke(msg)
        }
    }

    private suspend fun uploadVideo(msg: Msg, file: File, resultCallback: ((msg: Msg) -> Unit)?) {

        val result = UploadRepository.uploadVideo(file, file.name)
        if (result is UploadResult.Success) {
            if (msg.message is VideoMessage) {
                (msg.message as VideoMessage).url = result.fileUrl
            }
            IMCore.getService(MessageService::class.java).updateMessage(msg)
            sendMessage(msg, resultCallback = resultCallback)
        } else {
            updateMsgStatus(msg, MessageStatus.FAIL)
            resultCallback?.invoke(msg)
        }
    }


    private fun sendMessage(
        msg: Msg, giftPayload: String? = null, resultCallback: ((msg: Msg) -> Unit)?
    ) {
        viewModelScope.launch {
            val result = if (!giftPayload.isNullOrEmpty()) {
                _messageRepository.sendMessage(giftPayload)
            } else {
                _messageRepository.sendMessage(msg.toJson())
            }
            if (result.isSuccess) {
                updateMsgStatus(msg, MessageStatus.SUCCESS)
            } else {
                updateMsgStatus(msg, MessageStatus.FAIL)
            }
            resultCallback?.invoke(msg)
            IMCore.getService(ConversationService::class.java)
                .updateConversation(msg.sendId, msg.receiveId, msg, true)
        }
    }


    private suspend fun updateMsgStatus(msg: Msg, status: MessageStatus) {
        msg.status = status
        IMCore.getService(MessageService::class.java).updateMessage(msg, true)
    }


}