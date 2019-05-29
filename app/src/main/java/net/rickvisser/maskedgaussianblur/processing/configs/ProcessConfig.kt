package net.rickvisser.maskedgaussianblur.processing.configs

abstract class ProcessConfig(val type: ProcessType)

enum class ProcessType {
    BLUR
}