package s3492492project.labreportlogger.chandu.screens

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import s3492492project.labreportlogger.chandu.UserPrefs
import s3492492project.labreportlogger.chandu.ui.theme.crimsonRed
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val IMGBB_API_KEY = "dd2c6f23d315032050b31f06adcfaf3b"


data class LabReport(
    val reportId: String = "",
    val title: String = "",
    val category: String = "",
    val date: String = "",
    val images: List<String> = emptyList(),
    val isfavorite: Boolean = false
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadReportScreen() {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var reportName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }

    var images by remember { mutableStateOf(List<Uri?>(4) { null }) } // 4 slots
    var selectedSlot by remember { mutableStateOf(-1) }

    var isUploading by remember { mutableStateOf(false) }

    val categories = listOf(
        "Blood Test", "X-Ray / Imaging", "Prescription",
        "Vaccination Record", "ECG / Cardiac",
        "Discharge Summary", "Other Documents"
    )

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            calendar.set(y, m, d)
            selectedDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val readPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE

    fun hasReadPermission() =
        ContextCompat.checkSelfPermission(
            context,
            readPermission
        ) == PackageManager.PERMISSION_GRANTED

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                val list = images.toMutableList()
                list[selectedSlot] = it
                images = list
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) galleryLauncher.launch("image/*")
            else Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }

    fun pickImage(slot: Int) {
        selectedSlot = slot
        if (hasReadPermission()) {
            galleryLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(readPermission)
        }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Report", fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = crimsonRed,
                    titleContentColor = Color.White,
                )
            )
        }
    ) {

        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text("Report Name", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = reportName,
                onValueChange = { reportName = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Text("Category", fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expandedCategory)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                selectedCategory = it
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Date of Test", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, "")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            Text("Attach Up To 4 Images", fontWeight = FontWeight.Bold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .height(260.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(4) { index ->

                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEAEAEA))
                            .clickable { pickImage(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        val uri = images[index]
                        if (uri == null) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "",
                                tint = Color.Gray,
                                modifier = Modifier.size(40.dp)
                            )
                        } else {
                            Image(
                                painter = rememberAsyncImagePainter(uri),
                                contentDescription = "",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))


            Button(
                onClick = {
                    if (isUploading) return@Button

                    if (reportName.isBlank() || selectedCategory.isBlank() || selectedDate.isBlank()) {
                        Toast.makeText(context, "Fill all details", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val selectedImages = images.filterNotNull()
                    if (selectedImages.isEmpty()) {
                        Toast.makeText(context, "Select at least 1 image", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    isUploading = true

                    scope.launch(Dispatchers.IO) {

                        val uploadedUrls = mutableListOf<String>()

                        for (uri in selectedImages) {
                            val compressedBytes = compressImage(context, uri)
                            val url = uploadImageToImgBB(context, compressedBytes)
                            if (url != null) uploadedUrls.add(url)
                        }

                        uploadReportToFirebase(
                            context,
                            reportName,
                            selectedCategory,
                            selectedDate,
                            uploadedUrls
                        )

                        isUploading = false
                    }
                },
                enabled = !isUploading,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonColors(
                    containerColor = crimsonRed,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.LightGray
                )
            ) {
                if (isUploading)
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                else
                    Text("Upload", fontSize = 18.sp)
            }

            Spacer(Modifier.height(50.dp))
        }
    }
}

fun compressImage(context: Context, uri: Uri): ByteArray {

    val bitmap: Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val src = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(src)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

    var quality = 100
    var bytes: ByteArray

    do {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        bytes = stream.toByteArray()
        quality -= 10
    } while (bytes.size > 900_000 && quality > 20)

    return bytes
}

fun uploadImageToImgBB(context: Context, bytes: ByteArray): String? {

    val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
    val client = OkHttpClient()

    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("key", IMGBB_API_KEY)
        .addFormDataPart("image", base64)
        .build()

    val request = Request.Builder()
        .url("https://api.imgbb.com/1/upload")
        .post(body)
        .build()

    return try {
        val response = client.newCall(request).execute()
        val json = response.body?.string()
        Regex("\"url\":\"(.*?)\"").find(json ?: "")?.groups?.get(1)?.value
    } catch (e: IOException) {
        null
    }
}

fun uploadReportToFirebase(
    context: Context,
    title: String,
    category: String,
    date: String,
    images: List<String>
) {
    val db = FirebaseDatabase.getInstance().reference
    val email = UserPrefs.getEmail(context).replace(".", "_")
    val reportId = db.push().key ?: return

    val data = LabReport(
        reportId = reportId,
        title = title,
        category = category,
        date = date,
        images = images
    )

    db.child("Myreports").child(email).child(reportId)
        .setValue(data)
        .addOnSuccessListener {
            Toast.makeText(context, "Report Uploaded", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show()
        }
}
