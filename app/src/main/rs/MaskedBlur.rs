#pragma version(1)
#pragma rs java_package_name(net.rickvisser.maskedgaussianblur.renderscript)

static const char OFFSET[5] = { 0, 1, 2, 3, 4 };
static const float WEIGHT[5] = { 0.2734375, 0.21875, 0.109375, 0.03125, 0.00390625 };

rs_allocation gIn;
rs_allocation gMask;
uint32_t gWidth;
uint32_t gHeight;

static float4 sample(uint32_t x, uint32_t y, float4 defaultValue) {
    // Make sure that the sample data is inside of bounds.
    if (x < 0 || x >= gWidth || y < 0 || y >= gHeight) return defaultValue;

    uchar4 tmp = rsGetElementAt_uchar4(gIn, x, y);
    return rsUnpackColor8888(tmp);
}

uchar4 RS_KERNEL blur(uchar4 in, uint32_t x, uint32_t y) {
    // Get a color from the mask. If it is black, then return the current color.
    uchar4 mask = rsGetElementAt_uchar4(gMask, x, y);
    if (mask.r == 0) return in;

    // Convert in to float4.
    float4 f4In = rsUnpackColor8888(in);

    // Initialize the out variable.
    float4 out = f4In;
    out.r = 0;
    out.g = 0;
    out.b = 0;

    // Add the current pixel to out.
    out = f4In * WEIGHT[0];

    for (char i = 1; i < 5; i++) {
        // Get the defined constants.
        char offset = OFFSET[i];
        float weight = WEIGHT[i] / 2;

        // Add the pixel to the left.
        out += sample(x-offset, y, f4In) * weight;

        // Add the pixel to the right.
        out += sample(x+offset, y, f4In) * weight;

        // Add the pixel to the top.
        out += sample(x, y-offset, f4In) * weight;

        // Add the pixel to the bottom.
        out += sample(x, y+offset, f4In) * weight;
    }

    return rsPackColorTo8888(out);
}