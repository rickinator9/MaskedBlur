#pragma version(1)
#pragma rs java_package_name(net.rickvisser.maskedgaussianblur.renderscript)

rs_allocation gIn;
rs_allocation gSampled;
rs_allocation gMask;

uchar4 RS_KERNEL blur(uchar4 in, uint32_t x, uint32_t y) {
    // Get a color from the mask. If it is black, then return the current color.
    uchar4 mask = rsGetElementAt_uchar4(gMask, x, y);
    if (mask.r == 0) return in;

    uchar4 sampled = rsGetElementAt_uchar4(gSampled, x, y);
    return sampled;
}