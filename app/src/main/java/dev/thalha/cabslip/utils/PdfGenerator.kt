package dev.thalha.cabslip.utils

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.layout.borders.Border
import com.itextpdf.io.image.ImageDataFactory
import dev.thalha.cabslip.data.entity.CabInfo
import dev.thalha.cabslip.data.entity.Receipt
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateReceiptPdf(
        context: Context,
        receipt: Receipt,
        cabInfo: CabInfo
    ): String? {
        return try {
            val pdfDir = File(context.filesDir, "pdfs")
            if (!pdfDir.exists()) {
                pdfDir.mkdirs()
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = dateFormat.format(Date(receipt.tripStartDate))
            val fileName = "${receipt.receiptId}_$startDate.pdf"
            val pdfFile = File(pdfDir, fileName)

            val writer = PdfWriter(FileOutputStream(pdfFile))
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            // Add content to PDF
            addHeader(document, cabInfo)
            addReceiptDetails(document, receipt)
            addTripDetails(document, receipt)
            addFareBreakdown(document, receipt)
            addSignature(document, receipt.ownerSignaturePath)
            addFooter(document)

            document.close()
            pdfFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addHeader(document: Document, cabInfo: CabInfo) {
        // Create a table for header layout with logo (left) and cab info (right)
        val headerTable = Table(UnitValue.createPercentArray(floatArrayOf(25f, 75f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(20f)

        // Left cell - Logo
        val logoCell = Cell()
            .setBorder(Border.NO_BORDER)
        if (!cabInfo.logoPath.isNullOrBlank()) {
            try {
                val logoFile = File(cabInfo.logoPath)
                if (logoFile.exists()) {
                    val imageData = ImageDataFactory.create(cabInfo.logoPath)
                    val logoImage = Image(imageData)
                        .setWidth(80f)
                        .setHeight(80f)
                    logoCell.add(logoImage)
                } else {
                    // Placeholder if logo file doesn't exist
                    logoCell.add(Paragraph("LOGO").setFontSize(12f).setBold())
                }
            } catch (e: Exception) {
                // Fallback if logo loading fails
                logoCell.add(Paragraph("LOGO").setFontSize(12f).setBold())
            }
        } else {
            // No logo uploaded
            logoCell.add(Paragraph(""))
        }

        // Right cell - Cab information
        val infoCell = Cell()
            .setBorder(Border.NO_BORDER)

        // Cab name as title
        val title = Paragraph(cabInfo.cabName)
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(20f)
            .setBold()
            .setMarginBottom(5f)
        infoCell.add(title)

        // Address
        val address = Paragraph(cabInfo.cabAddress)
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(12f)
            .setMarginBottom(5f)
        infoCell.add(address)

        // Contact info
        val contact = StringBuilder()
        contact.append("Phone: ${cabInfo.primaryContact}")
        if (!cabInfo.secondaryContact.isNullOrBlank()) {
            contact.append(" | ${cabInfo.secondaryContact}")
        }
        contact.append("\nEmail: ${cabInfo.email}")

        val contactPara = Paragraph(contact.toString())
            .setTextAlignment(TextAlignment.LEFT)
            .setFontSize(10f)
        infoCell.add(contactPara)

        headerTable.addCell(logoCell)
        headerTable.addCell(infoCell)
        document.add(headerTable)

        // Separator line
        document.add(Paragraph("━".repeat(50))
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(15f))
    }

    private fun addReceiptDetails(document: Document, receipt: Receipt) {
        val receiptTitle = Paragraph("RECEIPT")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16f)
            .setBold()
            .setMarginBottom(10f)
        document.add(receiptTitle)

        val receiptId = Paragraph("Receipt ID: ${receipt.receiptId}")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(12f)
            .setBold()
            .setMarginBottom(15f)
        document.add(receiptId)

        // Trip dates
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        val startDate = dateFormat.format(Date(receipt.tripStartDate))
        val endDate = receipt.tripEndDate?.let { dateFormat.format(Date(it)) } ?: "Not specified"

        val tripDates = Paragraph("Trip: $startDate → $endDate")
            .setFontSize(10f)
            .setMarginBottom(10f)
        document.add(tripDates)
    }

    private fun addTripDetails(document: Document, receipt: Receipt) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(30f, 70f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(15f)

        // Helper function to add row
        fun addRow(label: String, value: String) {
            table.addCell(Cell().add(Paragraph(label).setFontSize(10f).setBold()))
            table.addCell(Cell().add(Paragraph(value).setFontSize(10f)))
        }

        addRow("Boarding Location:", receipt.boardingLocation)
        addRow("Destination:", receipt.destination)
        addRow("Vehicle Number:", receipt.vehicleNumber)

        if (receipt.driverName.isNotBlank()) {
            addRow("Driver Name:", receipt.driverName)
        }

        if (receipt.driverMobile.isNotBlank()) {
            addRow("Driver Mobile:", receipt.driverMobile)
        }

        addRow("Total Distance:", "${receipt.totalKm} km")
        addRow("Price per km:", "₹${String.format(Locale.getDefault(), "%.2f", receipt.pricePerKm)}")

        if (receipt.waitingHrs > 0) {
            addRow("Waiting Time:", "${receipt.waitingHrs} hours")
            addRow("Waiting Charge/hr:", "₹${String.format(Locale.getDefault(), "%.2f", receipt.waitingChargePerHr)}")
        }

        document.add(table)
    }

    private fun addFareBreakdown(document: Document, receipt: Receipt) {
        document.add(Paragraph("FARE BREAKDOWN")
            .setFontSize(12f)
            .setBold()
            .setMarginBottom(10f))

        val fareTable = Table(UnitValue.createPercentArray(floatArrayOf(60f, 40f)))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(15f)

        fun addFareRow(label: String, amount: Double, isBold: Boolean = false) {
            val labelCell = Cell().add(
                Paragraph(label).setFontSize(10f).apply {
                    if (isBold) setBold()
                }
            )
            val amountCell = Cell().add(
                Paragraph("₹${String.format(Locale.getDefault(), "%.2f", amount)}")
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .apply { if (isBold) setBold() }
            )
            fareTable.addCell(labelCell)
            fareTable.addCell(amountCell)
        }

        addFareRow("Base Fare (${receipt.totalKm} km × ₹${String.format(Locale.getDefault(), "%.2f", receipt.pricePerKm)})", receipt.baseFare)

        if (receipt.waitingFee > 0) {
            addFareRow("Waiting Fee (${receipt.waitingHrs} hrs × ₹${String.format(Locale.getDefault(), "%.2f", receipt.waitingChargePerHr)})", receipt.waitingFee)
        }

        if (receipt.tollParking > 0) {
            addFareRow("Toll & Parking", receipt.tollParking)
        }

        if (receipt.bata > 0) {
            addFareRow("Bata", receipt.bata)
        }

        // Add separator
        fareTable.addCell(Cell(1, 2).add(Paragraph("━".repeat(30)).setFontSize(8f)))

        // Total
        addFareRow("TOTAL FEE", receipt.totalFee, true)

        document.add(fareTable)
    }

    private fun addSignature(document: Document, signaturePath: String?) {
        if (!signaturePath.isNullOrBlank()) {
            try {
                val signatureFile = File(signaturePath)
                if (signatureFile.exists()) {
                    val imageData = ImageDataFactory.create(signaturePath)
                    val image = Image(imageData)
                        .setWidth(150f)
                        .setHeight(75f)

                    document.add(Paragraph("Owner Signature:")
                        .setFontSize(10f)
                        .setBold()
                        .setMarginTop(20f)
                        .setMarginBottom(5f))

                    document.add(image)
                }
            } catch (_: Exception) {
                // If signature loading fails, add placeholder
                document.add(Paragraph("Owner Signature: ________________")
                    .setFontSize(10f)
                    .setMarginTop(20f))
            }
        } else {
            document.add(Paragraph("Owner Signature: ________________")
                .setFontSize(10f)
                .setMarginTop(20f))
        }
    }

    private fun addFooter(document: Document) {
        document.add(Paragraph("Thank you for choosing our service!")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(8f)
            .setMarginTop(30f))

        document.add(Paragraph("Generated by CabSlip")
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(6f)
            .setMarginTop(5f))
    }
}
