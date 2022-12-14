package com.hitesh.mylibrary.utils

import android.util.Log
import com.hitesh.mylibrary.model.CardDetails

internal object Extractor {

    fun extractData(input: String): CardDetails? {
        val lines = input.split("\n")
        Log.d("CardNumber", lines.joinToString())

        val number = extractNumber(lines) ?: return null
        Log.d("CardNumber", number)

        val (month, year) = extractExpiration(lines) ?: return null
        Log.d("CardMonth", month.toString())
        Log.d("CardYear", year.toString())

        val owner = extractOwner(lines)
        Log.d("CardOwner", owner.toString())

        return CardDetails(
            owner = owner,
            number = number,
            expirationMonth = month,
            expirationYear = year
        )
    }

    private fun extractOwner(lines: List<String>): String? {
        return lines
            .filter { it.contains(" ") }
            .filter { line -> line.asIterable().none { char -> char.isDigit() } }
            .maxByOrNull { it.length }
    }

    private fun extractNumber(lines: List<String>): String? {
        return lines.firstOrNull { line ->
            val subNumbers = line.split(" ")
            subNumbers.isNotEmpty() && subNumbers.flatMap { it.asIterable() }.all { it.isDigit() }
        }
    }

    private fun extractExpiration(lines: List<String>): Pair<String?, String?>? {
        val expirationLine = extractExpirationLine(lines) ?: return null

        val month = expirationLine.substring(startIndex = 0, endIndex = 2)
        val year = expirationLine.substring(startIndex = 3)
        return Pair(month, year)
    }

    private fun extractExpirationLine(lines: List<String>) =
        lines.flatMap { it.split(" ") }
            .lastOrNull { (it.length == 5 || it.length == 7) && it[2] == '/' }


}