package com.mat.zakupnik.handlers

import android.util.Log
import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
import java.time.LocalDate

class FileHandler {

    companion object {
        private const val TAG = "FileHandler"

        class LocalDateSerializer : JsonSerializer<LocalDate> {
            override fun serialize(
                date: LocalDate?,
                typeOfSrc: Type?,
                context: JsonSerializationContext?
            ): JsonElement {
                return JsonPrimitive(date?.toString())
            }

        }

        class LocalDateDeserializer : JsonDeserializer<LocalDate> {
            override fun deserialize(
                json: JsonElement?,
                typeOfT: Type?,
                context: JsonDeserializationContext?
            ): LocalDate {
                return LocalDate.parse(json?.asJsonPrimitive?.asString)
            }

        }


        private fun createFile(file : String) : Boolean {
            val fileName = file.split(Regex("/")).last()
            return if (File(file).createNewFile()) {
                Log.i(TAG, "$fileName file created.")
                true
            } else {
                Log.i(TAG, "File $fileName wasn't created or already exists.")
                false
            }
        }

        fun read(path: String) : String {
            createFile(path)
            var data = String()
            File(path).forEachLine { data += it }
            return data
        }

        fun write(data: String, path: String) {
            createFile(path)
            File(path).writeText(data)
        }

        inline fun <reified T> readJson(path: String): T {
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                .create()

            val fileDataString = read(path)
            return gson.fromJson(fileDataString, T::class.java)
        }
        fun <T> writeJson(obj: T, path: String) {
            val gson = GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
                .create()

            createFile(path)
            File(path).writeText(gson.toJson(obj))
        }

    }
}