package net.rickvisser.maskedgaussianblur.processing.configs

/**
 * Abstract class to implement configurations containing parameters for image processing operations.
 * The type for each config should be unique.
 */
abstract class ProcessConfig(val type: ProcessType)

/**
 * Enum class with process types to be used in configs.
 */
enum class ProcessType {
    BLUR,
    CENSOR
}