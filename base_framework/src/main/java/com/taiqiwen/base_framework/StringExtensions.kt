package com.taiqiwen.base_framework

fun String?.isNotNullOrEmpty(): Boolean {
    return this != null && length > 0
}