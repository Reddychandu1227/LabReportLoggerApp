package madproject.chandu.labreportlogger.screens

// your model (import or define it in same package)
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailsScreen(
    report: LabReport,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        isLoading = true     // show loader
                        val ok = generatePdfAndShare(context, report)
                        isLoading = false    // hide loader

                        if (!ok) {
                            Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                containerColor = Color.Black
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text("Report Details", fontSize = 22.sp, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { padding ->

        // ---------- SHOW LOADING DIALOG ----------
        if (isLoading) {
            LoadingDialog()
        }

        // ---------------- UI CONTENT ----------------
        ReportDetailsContent(report = report, padding = padding)
    }
}


@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { }) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(strokeWidth = 4.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Generating PDF...", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}


@Composable
fun ReportDetailsContent(
    report: LabReport,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // ---------------- PREMIUM HEADER ----------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFA5A62),
                            Color(0xFFFFB3B8)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = report.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = report.category,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- DETAILS CARD ----------------
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.elevatedCardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    "Report ID: ${report.reportId}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Category: ${report.category}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    "Date: ${report.date}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ---------------- IMAGES TITLE ----------------
        Text(
            "Attached Images",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(Modifier.height(14.dp))

        // ---------------- IMAGE LIST ----------------
        if (report.images.isEmpty()) {
            Text(
                text = "No images attached",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray,
                fontSize = 16.sp
            )
        } else {
            report.images.forEach { imageUrl ->
                ElevatedCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}


suspend fun generatePdfAndShare(context: Context, report: LabReport): Boolean {
    return try {
        withContext(Dispatchers.IO) {

            // Create PDF
            val document = PdfDocument()
            var pageNumber = 1

            // Page 1
            val pageInfo = PdfDocument.PageInfo.Builder(1120, 1500, pageNumber).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paintTitle = android.graphics.Paint().apply {
                textSize = 50f
                isFakeBoldText = true
            }

            val paintBody = android.graphics.Paint().apply {
                textSize = 32f
            }

            canvas.drawText(report.title, 50f, 100f, paintTitle)
            canvas.drawText("Category: ${report.category}", 50f, 180f, paintBody)
            canvas.drawText("Date: ${report.date}", 50f, 240f, paintBody)
            canvas.drawText("Report ID: ${report.reportId}", 50f, 300f, paintBody)

            document.finishPage(page)
            pageNumber++

            // Download image bitmaps
            val client = OkHttpClient()
            for (url in report.images) {
                val req = Request.Builder().url(url).build()
                val resp = client.newCall(req).execute()
                val bytes = resp.body?.bytes()
                resp.close()

                if (bytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        val pageImg = PdfDocument.PageInfo.Builder(1120, 1500, pageNumber).create()
                        val imgPage = document.startPage(pageImg)
                        val canvasImg = imgPage.canvas

                        val scaled = Bitmap.createScaledBitmap(
                            bitmap,
                            1000,
                            (bitmap.height * (1000f / bitmap.width)).toInt(),
                            true
                        )

                        val left = (pageImg.pageWidth - scaled.width) / 2f
                        canvasImg.drawBitmap(scaled, left, 100f, null)

                        document.finishPage(imgPage)
                        pageNumber++
                    }
                }
            }

            // Save PDF to Downloads (no XML needed)
            val fileName = "Report_${report.reportId}.pdf"
            val resolver = context.contentResolver

            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
            }

            val pdfUri = resolver.insert(
                android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

            if (pdfUri == null) return@withContext false

            resolver.openOutputStream(pdfUri)?.use { out ->
                document.writeTo(out)
            }

            contentValues.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(pdfUri, contentValues, null, null)

            document.close()

            // Share using Intent
            withContext(Dispatchers.Main) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, pdfUri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                context.startActivity(
                    Intent.createChooser(intent, "Share Report PDF").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }

            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
