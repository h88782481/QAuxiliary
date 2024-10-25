/*
 * QAuxiliary - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2024 QAuxiliary developers
 * https://github.com/cinit/QAuxiliary
 *
 * This software is an opensource software: you can redistribute it
 * and/or modify it under the terms of the General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version as published
 * by QAuxiliary contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the General Public License for more details.
 *
 * You should have received a copy of the General Public License
 * along with this software.
 * If not, see
 * <https://github.com/cinit/QAuxiliary/blob/master/LICENSE.md>.
 */

package me.hd.hook

import android.view.View
import android.widget.ImageView
import cc.ioctl.util.hookAfterIfEnabled
import io.github.qauxv.base.annotation.FunctionHookEntry
import io.github.qauxv.base.annotation.UiItemAgentEntry
import io.github.qauxv.dsl.FunctionEntryRouter
import io.github.qauxv.util.Initiator
import io.github.qauxv.util.QQVersion
import io.github.qauxv.util.requireMinQQVersion
import xyz.nextalone.base.MultiItemDelayableHook

@FunctionHookEntry
@UiItemAgentEntry
object HideChatPanelBtn : MultiItemDelayableHook(
    keyName = "hd_HideChatPanelBtn"
) {
    override val preferenceTitle = "屏蔽聊天面板按钮"
    override val allItems = setOf("语音", "拍照", "红包", "表情", "更多功能")
    override val defaultItems = setOf<String>()
    override val uiItemLocation = FunctionEntryRouter.Locations.Simplify.CHAT_OTHER
    override val isAvailable = requireMinQQVersion(QQVersion.QQ_8_9_88)

    override fun initOnce(): Boolean {
        val panelIconClass = Initiator.loadClass("com.tencent.qqnt.aio.shortcutbar.PanelIconLinearLayout")
        val iconItemMethod = panelIconClass.declaredMethods.single { method ->
            method.returnType == ImageView::class.java
        }
        hookAfterIfEnabled(iconItemMethod) { param ->
            val imageView = param.result as ImageView
            val contentDesc = imageView.contentDescription
            if (activeItems.contains(contentDesc)) {
                imageView.visibility = View.GONE
            }
        }
        return true
    }
}