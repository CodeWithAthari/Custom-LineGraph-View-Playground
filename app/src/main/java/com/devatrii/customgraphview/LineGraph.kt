package com.devatrii.customgraphview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator


class LineGraph : View {
    private var mPaint: Paint
    private var mIsInit: Boolean = false
    private var mPath: Path
    private var funPath: Path

    private var mOriginX: Float = 0.0f
    private var mOriginY: Float = 0.0f
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mXUnit: Float = 0.0f
    private var mYUnit: Float = 0.0f
    private var mBlackPaint: Paint
    private var mDataPoints: ArrayList<Float>? = null
    private val TAG = "LineGraph"

    fun setDataPoints(mDataPoints: ArrayList<Float>) {
        this.mDataPoints = mDataPoints
    }

    constructor(context: Context) : super(context) {
        mPaint = Paint()
        mPath = Path()
        funPath = Path()
        mBlackPaint = Paint()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mPaint = Paint()
        mPath = Path()
        funPath = Path()
        mBlackPaint = Paint()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mPaint = Paint()
        mPath = Path()
        funPath = Path()
        mBlackPaint = Paint()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context, attrs, defStyleAttr, defStyleRes
    ) {
        mPaint = Paint()
        mPath = Path()
        funPath = Path()
        mBlackPaint = Paint()
    }

    private fun init() {
        mPaint = Paint()
        mPath = Path()
        mWidth = width
        mHeight = height
        funPath = Path()
        mXUnit = (mWidth / 12).toFloat() //for 10 plots on x axis, 2 kept for padding;
        mYUnit = (mHeight / 12).toFloat()
        mOriginX = mXUnit
        mOriginY = mHeight - mYUnit
        mBlackPaint = Paint()
        mIsInit = true
    }

    private fun drawAxis(canvas: Canvas, paint: Paint) {
        Log.i(TAG, "drawAxis: Height: $mHeight, Width: $mWidth")
        Log.i(TAG, "drawAxis: xUnit: $mXUnit, yUnit:$mYUnit")

        canvas.drawLine(mXUnit, mYUnit, mXUnit, (mHeight - mYUnit), paint) //y-axis

        canvas.drawLine(
            mXUnit, mHeight - mYUnit, mWidth - mXUnit, mHeight - mYUnit, paint
        ) //x-axis
    }

    private fun drawGraphPlotLines(canvas: Canvas, path: Path, paint: Paint) {
        var originX = mXUnit
        val originY = mHeight - mYUnit
        path.reset()
        Log.i(TAG, "drawGraphPlotLines: OriginX: $originX, OriginY:$originY")
        path.moveTo(originX, originY) // Shift origin to graph's origin

        var previousX = 0f
        var previousY = 0f
        for (i in 0 until mDataPoints!!.size) {
            if (i == 0) {
                path.moveTo(mXUnit, mHeight - (mHeight/4).toFloat())
            }
            val lineX = originX + mXUnit
            val lineY = originY - (mDataPoints!![i] * mYUnit)
            Log.i(
                TAG,
                "drawGraphPlotLines: LineX: $lineX, LineY: $lineY => DataPoint:${mDataPoints!![i]}"
            )
            if (i < mDataPoints!!.size - 1) {
                val nextOriginX = originX + mXUnit
                val nextX = nextOriginX + mXUnit
                val nextY = originY - (mDataPoints!![i + 1] * mYUnit)

                // Calculate control points for cubic Bezier curve
                val controlPoint1X = lineX + mXUnit * 0.5f
                val controlPoint1Y = lineY
                val controlPoint2X = nextX - mXUnit * 0.5f
                val controlPoint2Y = nextY
                path.cubicTo(controlPoint1X, controlPoint1Y, controlPoint2X, controlPoint2Y, nextX, nextY)
            } else {
                path.lineTo(lineX, lineY)
            }
            previousX = lineX
            previousY = lineY
            originX += mXUnit
//            canvas.drawCircle(lineX,lineY,15f,paint)
        } // end for

        canvas.drawPath(path, paint)
    }
    val path = Path()
    val color1 = Color.argb((0.44 * 255).toInt(), 190, 174, 229) // BEAEE5 with 44% opacity
    val color2 = Color.TRANSPARENT

    // Define the coordinates for the gradient
    val x0 = 0f // Start X-coordinate (left)
    val y0 = 0f // Start Y-coordinate (top)
    val x1 = 0f // End X-coordinate (left, since it's a linear gradient)
    val y1 = (mHeight - mYUnit).toFloat() // End Y-coordinate (bottom)
    val linearGradient = LinearGradient(x0, y0, x1, y1, color1, color2, Shader.TileMode.CLAMP)

    private fun fillPlot(canvas: Canvas) {
        var originX = mXUnit
        var originY = mHeight - mYUnit

        val paint = Paint().apply {
            setShader(
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height.toFloat(),
                    color1,
                    Color.WHITE,
                    Shader.TileMode.MIRROR
                )
            )
            strokeWidth = 5f // Set your desired stroke width
            style = Paint.Style.FILL
            isAntiAlias = true // Enable anti-aliasing for smoother edges
        }

        path.reset() // Reset the path to start fresh for each call to fillPlot

        for (i in 0 until mDataPoints!!.size) {
            if (i == 0) {
                path.moveTo(mXUnit, mHeight - (mHeight/4).toFloat())
            }
            val lineX = originX + mXUnit
            val lineY = originY - (mDataPoints!![i] * mYUnit)
            Log.i(
                TAG,
                "drawGraphPlotLines: LineX: $lineX, LineY: $lineY => DataPoint:${mDataPoints!![i]}"
            )

            if (i < mDataPoints!!.size - 1) {
                val nextOriginX = originX + (mXUnit * 1)
                val nextX = nextOriginX + mXUnit
                val nextY = originY - (mDataPoints!![i + 1] * mYUnit)
                // Calculate control points for cubic Bezier curve
                val controlPoint1X = lineX + mXUnit * 0.5f
                val controlPoint1Y = lineY
                val controlPoint2X = nextOriginX + mXUnit * 0.5f
                val controlPoint2Y = nextY
                path.cubicTo(controlPoint1X, controlPoint1Y, controlPoint2X, controlPoint2Y, nextX, nextY)
            }
            originX += mXUnit
        } //end for

        // Complete the path by adding the closing lines
        path.lineTo(mWidth - mXUnit, mHeight - mYUnit)
        path.lineTo(mXUnit, (mHeight - mYUnit))

        canvas.drawPath(path, paint)
    }

    private fun drawGraphPaper(canvas: Canvas, blackPaint: Paint) {
        var cx = mXUnit
        var cy = mHeight - mYUnit
        blackPaint.strokeWidth = 1f
        for (i in 1..11) {
            canvas.drawLine(cx, mYUnit, cx, cy, blackPaint)
            cx += mXUnit
        } //drawing points on x axis(vertical lines)
        cx = mXUnit
        for (i in 1..11) {
            canvas.drawLine(cx, cy, mWidth - mXUnit, cy, blackPaint)
            cy -= mYUnit
        } //drawing points on y axis
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (!mIsInit) {
            init()
        }

        mBlackPaint.color = Color.BLACK;
        mBlackPaint.style = Paint.Style.STROKE;
        mBlackPaint.strokeWidth = 5f;
        mBlackPaint.apply {
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        mPaint.style = Paint.Style.STROKE;
        mPaint.strokeWidth = 10F;
        mPaint.color = Color.BLUE;

        drawAxis(canvas!!, mBlackPaint);
//        moveToPlayground(canvas)


        fillPlot(canvas)
        drawGraphPlotLines(canvas, mPath, mBlackPaint);
        drawGraphPaper(canvas, mBlackPaint);
        drawTextOnXaxis(canvas, mBlackPaint);
//        drawTextOnYaxis(canvas, mBlackPaint);
//        canvasPlayground(canvas)
        invalidate()
    }

    fun moveToPlayground(canvas: Canvas) {
        val pt1 = PointF(0f, 0f)
        val pt2 = PointF(width.toFloat(), height.toFloat())

        val mid = PointF(width.toFloat(), height.toFloat() / 2)

        val path = Path()
        path.moveTo(mXUnit, mHeight - 500f)
        path.quadTo((mWidth / 2).toFloat(), mYUnit, mXUnit, mHeight - mYUnit)
//        path.quadTo((mWidth/2).toFloat(), mYUnit, (mWidth/2).toFloat(), mYUnit)
//        path.moveTo(mXUnit,mHeight-mYUnit)
        canvas.drawPath(path, mBlackPaint)
    }

    fun canvasPlayground(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        val funPath = Path()
        funPath.moveTo((width / 2).toFloat(), height.toFloat())
        funPath.lineTo((width / 2).toFloat(), height.toFloat())
        funPath.lineTo((width / 2).toFloat(), 0f)

        val mBlackPaint = Paint().apply {
            color = Color.RED
            strokeWidth = 20f
            style = Paint.Style.STROKE
            textSize = 40f
            isFakeBoldText = true
        }

        val animPath = Path() // The animated path that will gradually draw the original path
        val pathMeasure = PathMeasure(funPath, false)

        val pathLength = pathMeasure.length
        val valueAnimator = ValueAnimator.ofFloat(0f, pathLength)

        valueAnimator.apply {
            duration = 2000 // Change the duration to control the animation speed
            interpolator = LinearInterpolator()
            addUpdateListener {
                val value = it.animatedValue as Float
                pathMeasure.getSegment(0f, value, animPath, true)
                canvas.drawPath(animPath, mBlackPaint)
            }
        }

        valueAnimator.start()
    }

    private fun drawTextOnXaxis(canvas: Canvas, paint: Paint) {
        var originX = mXUnit
        var originY = mHeight
        val dataset = mDataPoints!!
        val newPaint = Paint().apply {
            color = Color.RED
            strokeWidth = 20f
            style = Paint.Style.FILL
            color = Color.BLACK;
            textSize = 40f;
            isFakeBoldText = true
        }
        for (i in 0 until dataset.size) {
            val dataValue = dataset[i]
            val xPosition = originX + mXUnit
            val yPosition = (originY - mYUnit) + (mYUnit / 1.5).toFloat()
//            canvas.drawCircle(xPosition, yPosition, 10f, newPaint)
            canvas.drawText("${dataValue.toInt()}", xPosition - 10f, yPosition, newPaint)

            originX += mXUnit
        }

    }

}















