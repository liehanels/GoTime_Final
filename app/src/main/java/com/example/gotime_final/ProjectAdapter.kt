package com.example.gotime_final

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Window
import androidx.core.content.ContextCompat.startActivity

class GraphView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val dataSet = mutableListOf<DataPoint>()
    private var xMin = 0
    private var xMax = 0
    private var yMin = 0
    private var yMax = 0

    private val dataPointPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        style = Paint.Style.STROKE
    }

    private val dataPointFillPaint = Paint().apply {
        color = Color.WHITE
    }

    private val dataPointLinePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 7f
        isAntiAlias = true
    }

    private val axisLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
    }

    fun setData(newDataSet: List<DataPoint>) {
        xMin = newDataSet.minBy { it.xVal }?.xVal ?: 0
        xMax = newDataSet.maxBy { it.xVal }?.xVal ?: 0
        yMin = newDataSet.minBy { it.yVal }?.yVal ?: 0
        yMax = newDataSet.maxBy { it.yVal }?.yVal ?: 0
        dataSet.clear()
        dataSet.addAll(newDataSet)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        dataSet.forEachIndexed { index, currentDataPoint ->
            val realX = currentDataPoint.xVal.toRealX()
            val realY = currentDataPoint.yVal.toRealY()

            if (index < dataSet.size - 1) {
                val nextDataPoint = dataSet[index + 1]
                val startX = currentDataPoint.xVal.toRealX()
                val startY = currentDataPoint.yVal.toRealY()
                val endX = nextDataPoint.xVal.toRealX()
                val endY = nextDataPoint.yVal.toRealY()
                canvas.drawLine(startX, startY, endX, endY, dataPointLinePaint)
            }

            canvas.drawCircle(realX, realY, 7f, dataPointFillPaint)
            canvas.drawCircle(realX, realY, 7f, dataPointPaint)
        }

        canvas.drawLine(0f, 0f, 0f, height.toFloat(), axisLinePaint)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), axisLinePaint)
    }

    private fun Int.toRealX() = toFloat() / xMax * width

    private fun Int.toRealY() = toFloat() / yMax * height

}

data class DataPoint(
    val xVal: Int,
    val yVal: Int
)

class ProjectAdapter :ListAdapter<Project, ProjectAdapter.ProjectAdapter>(ProjectViewHolder())
{
    private lateinit var dialog: Dialog

    class ProjectAdapter(view : View):RecyclerView.ViewHolder(view){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter {
        val inflater = LayoutInflater.from(parent.context)
        return com.example.gotime_final.ProjectAdapter.ProjectAdapter(inflater.inflate(
            R.layout.project_layout,parent,false))
    }
    override fun onBindViewHolder(holder: ProjectAdapter, position: Int) {
        val project = getItem(position)
        //Declaring executor to parse the url
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        //initializing the image
        var image: Bitmap? = null
        val imageView = holder.itemView.findViewById<ImageView>(R.id.ImgProject)
        executor.execute {
            //image url
            val imageUrl = "C:\\Users\\owner\\AndroidStudioProjects\\GoTime_Final\\app\\src\\main\\res\\drawable\\gotime_logo.png"
            //Tries to get the image and post
            // it in the imageview with the help of the handler
            try {
                val `in` = java.net.URL(imageUrl).openStream()
                image = BitmapFactory.decodeStream(`in`)
                //only for making changes in UI
                handler.post {
                    imageView.setImageBitmap(image)
                }

            } catch (e: java.lang.Exception) {

            }
        }
        holder.itemView.findViewById<TextView>(R.id.tvProjectName).text = project.Name
        holder.itemView.findViewById<TextView>(R.id.tvProjectDescription).text = project.Description

        val btnviewStats = holder.itemView.findViewById<Button>(R.id.btnViewStats).setOnClickListener{
            val intent = Intent(holder.itemView.context, ViewProjectStats::class.java)
            holder.itemView.context.startActivity (intent)
        }
        holder.itemView.findViewById<Button>(R.id.btnEditProject).setOnClickListener{//button click listener
            val builder = AlertDialog.Builder(holder.itemView.context)
            var userSelection = ""
            // set the title of the dialog
            builder.setTitle("Update or Delete Project ? :")
            // set the positive button with its text and listener
            builder.setNeutralButton("Update") { dialogInterface, which ->
                // get the text of the button and assign it to userSelection
                userSelection = (dialogInterface as AlertDialog).getButton(which).text.toString()
                val intent = Intent(holder.itemView.context, EditProject::class.java)
                holder.itemView.context.startActivity (intent)
            }
            // set the neutral button with its text and listener
            builder.setNegativeButton("Cancel") { dialogInterface, which ->
                // cancel the dialog
                dialogInterface.cancel()
            }
            // set the negative button with its text and listener
            builder.setPositiveButton("Delete") { dialogInterface, which ->
                // get the text of the button and assign it to userSelection
                userSelection = (dialogInterface as AlertDialog).getButton(which).text.toString()
                // do something with userSelection, such as showing a toast message
                val builder = AlertDialog.Builder(holder.itemView.context)
                var userSelection = ""
                // set the title of the dialog
                builder.setTitle("Are you sure you'd like to delete project :") //add project name ?

                // set the positive button with its text and listener
                builder.setPositiveButton("Yes") { dialogInterface, which ->
                    // get the text of the button and assign it to userSelection
                    userSelection = (dialogInterface as AlertDialog).getButton(which).text.toString()
                    Toast.makeText(holder.itemView.context, "Project Deleted $userSelection", Toast.LENGTH_SHORT).show()
                }

                // set the neutral button with its text and listener
                builder.setNeutralButton("Cancel") { dialogInterface, which ->
                    // cancel the dialog
                    Toast.makeText(holder.itemView.context, "Cancelled", Toast.LENGTH_SHORT).show()
                    dialogInterface.cancel()
                }

                // set the negative button with its text and listener
                builder.setNegativeButton("No") { dialogInterface, which ->
                    // get the text of the button and assign it to userSelection
                    userSelection = (dialogInterface as AlertDialog).getButton(which).text.toString()
                    Toast.makeText(holder.itemView.context, "You selected not to delete the project.", Toast.LENGTH_SHORT).show()
                }
                // show the dialog
                builder.show()
            }
            // show the dialog
            builder.show()
        }
        //edit project
        holder.itemView.findViewById<Button>(R.id.btnAddHours).setOnClickListener{
            val builder = AlertDialog.Builder(holder.itemView.context)
            var userInput = ""
            // set the title of the dialog
            builder.setTitle("Select Capture Mode :")
            // set the positive button with its text and listener
            builder.setPositiveButton("Auto Time Tracking") { dialogInterface, which ->
                // get the text of the button and assign it to userSelection
                userInput = (dialogInterface as AlertDialog).getButton(which).text.toString()
                // do something with userSelection, such as showing a toast message
                Toast.makeText(holder.itemView.context, "You selected $userInput", Toast.LENGTH_SHORT).show()
            }
            // set the neutral button with its text and listener
            builder.setNeutralButton("Cancel") { dialogInterface, which ->
                // cancel the dialog
                dialogInterface.cancel()
            }
            // set the negative button with its text and listener
            builder.setNegativeButton("Manual Time Tracking") { dialogInterface, which ->
                // get the text of the and assign it to userSelection
                userInput = (dialogInterface as AlertDialog).getButton(which).text.toString()
                // do something with userSelection, such as showing a toast message
                Toast.makeText(holder.itemView.context, "You selected $userInput", Toast.LENGTH_SHORT).show()
            }
// show the dialog
            builder.show()
        }
    }
}
class ProjectViewHolder : DiffUtil.ItemCallback<Project>()
{
    override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
        return oldItem.Name == newItem.Name
    }

    override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
        return areItemsTheSame(oldItem,newItem)
    }
}