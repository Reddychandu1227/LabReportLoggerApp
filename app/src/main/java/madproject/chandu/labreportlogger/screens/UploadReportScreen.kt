import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import madproject.chandu.labreportlogger.UserPrefs
import madproject.chandu.labreportlogger.ui.theme.crimsonRed
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadReportScreen(
    onBack: () -> Unit = {},
    onUpload: (reportName: String, category: String, date: String, imageUri: Uri?) -> Unit =
        { _, _, _, _ -> }
) {

    val context = LocalContext.current

    // ---------------- STATE --------------------
    var reportName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()) }

    val categories = listOf(
        "Blood Test", "X-Ray / Imaging", "Prescription", "Vaccination Record",
        "ECG / Cardiac", "Discharge Summary", "Other Documents"
    )

    // ----------------- DATE PICKER --------------------
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // ------------- PERMISSIONS -----------------------
    val cameraPermission = Manifest.permission.CAMERA
    val readImagesPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

    // Permission helper
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    // ---------------- LAUNCHERS ------------------------

    // GALLERY launcher
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { selectedImageUri = it }
        }

    // CAMERA launcher (bitmap)
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                selectedImageUri = saveBitmapToCacheNoXml(context, bitmap)
            }
        }

    // Permission: Storage/Gallery
    val storagePermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                galleryLauncher.launch("image/*")
            } else {
                Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Permission: Camera
    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                cameraLauncher.launch(null)
            } else {
                Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // ---------------- UI STARTS ------------------------
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Report",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = crimsonRed,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------- REPORT NAME ----------

            item {
                Text("Report Name", fontWeight = FontWeight.SemiBold)

            }
            item {

                OutlinedTextField(
                    value = reportName,
                    onValueChange = { reportName = it },
                    placeholder = { Text("Enter report name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                // ---------- CATEGORY ----------
                Text("Category", fontWeight = FontWeight.SemiBold)
            }
            item {
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
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
            }
            item {
                // ---------- DATE ----------
                Text("Date of Test", fontWeight = FontWeight.SemiBold)

            }
            item {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select Date") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {

                // ---------- IMAGE UPLOAD ----------
                Text("Attach Report Image", fontWeight = FontWeight.SemiBold)

            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // Gallery Button
                    ElevatedCard(
                        modifier = Modifier
                            .size(width = 150.dp, height = 55.dp)
                            .clickable {
                                if (hasPermission(readImagesPermission)) {
                                    galleryLauncher.launch("image/*")
                                } else {
                                    storagePermissionLauncher.launch(readImagesPermission)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE7F2FF))
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = Color(0xFF1565C0)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Gallery",
                                color = Color(0xFF1565C0),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Camera Button
                    ElevatedCard(
                        modifier = Modifier
                            .size(width = 150.dp, height = 55.dp)
                            .clickable {
                                if (hasPermission(cameraPermission)) {
                                    cameraLauncher.launch(null)
                                } else {
                                    cameraPermissionLauncher.launch(cameraPermission)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Row(
                            Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFFD32F2F)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Camera",
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

            }
            item {

                // Preview image
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

            }
            item {
                // ---------- UPLOAD BUTTON ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (reportName.isBlank() || selectedCategory.isBlank() || selectedDate.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "All fields are mandatory",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
//                                onUpload(
//                                    reportName,
//                                    selectedCategory,
//                                    selectedDate,
//                                    selectedImageUri
//                                )

                                uploadLabreports(
                                    LabreportData(
                                        reportName,
                                        selectedCategory,
                                        selectedDate
                                    ),
                                    context
                                )
                            }
                        },
                        modifier = Modifier
                            .width(160.dp)
                            .height(50.dp)
                    ) {
                        Text("Upload", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


fun saveBitmapToCacheNoXml(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "img_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return Uri.fromFile(file)
}


@Preview(showBackground = true)
@Composable
fun UploadMedicalReportScreenPreview() {
    MaterialTheme {
        UploadReportScreen()
    }
}


data class LabreportData(
    val reportTitle: String = "",
    val reportCategory: String = "",
    val date: String = "",
    val reportID: String = "",
    val imageUrl: String = "",
    val time: String = ""
)

fun uploadLabreports(
    reportData: LabreportData,
    context: Context
) {
    val databaseRef = FirebaseDatabase.getInstance().reference
    val userEmail = UserPrefs.getEmail(context = context)
    val reportId = databaseRef.child("LabReportPosts").push().key ?: return

    Toast.makeText(context, "1", Toast.LENGTH_SHORT).show()


    val reportMap = mapOf(
        "reportId" to reportId,
        "reportTitle" to reportData.reportTitle,
        "reportCategory" to reportData.reportCategory,
        "imageUrl" to "https://thumbs.dreamstime.com/b/breaking-news-global-updates-insights-wallpaper-366068573.jpg",

        "date" to reportData.date,

        )

    Toast.makeText(context, "2", Toast.LENGTH_SHORT).show()

    Log.e("test", "1")
    databaseRef.child("Myreports/$userEmail").child(reportId)
        .setValue(reportMap)
        .addOnSuccessListener {
            Toast.makeText(context, "Posted Reports Successfully.", Toast.LENGTH_SHORT).show()
//            (context as Activity).finish()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to save reportsn.", Toast.LENGTH_SHORT).show()
        }
}