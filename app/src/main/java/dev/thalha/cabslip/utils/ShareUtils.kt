package dev.thalha.cabslip.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object ShareUtils {

    fun sharePdf(context: Context, pdfPath: String, receiptId: String) {
        try {
            val file = File(pdfPath)
            if (!file.exists()) return

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Receipt $receiptId")
                putExtra(Intent.EXTRA_TEXT, "Please find the attached receipt for trip $receiptId")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Share Receipt")
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
