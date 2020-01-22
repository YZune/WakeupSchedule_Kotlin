package com.suda.yzune.wakeupschedule.schedule_import.hust

import java.math.BigInteger

val HUST_RSA_EXPONENT = BigInteger("10001", 16)

class Cipher(private var exponent: BigInteger, private var modulus: BigInteger) {

    private val chunkSize: Int = modulus.bitLength() / 8

    @kotlin.ExperimentalUnsignedTypes
    fun encrypt(input: String): String {
        val result = StringBuilder()

        input.padEnd(input.length + input.length % chunkSize, 0.toChar())

        val bytes = ByteArray(input.length + chunkSize - (input.length % chunkSize)) { 0 }
        input.toByteArray().copyInto(bytes) // padding to chunkSize

        for (bstart in bytes.indices step chunkSize) {
            var block = BigInteger.ZERO

            //println(bytes.size)
            for (i in bstart + chunkSize - 1 downTo bstart + 1 step 2) {
                block = block.shiftLeft(16)
                val v = bytes[i].toShort() * 256 + bytes[i - 1].toShort()
                block = block.add(BigInteger.valueOf(v.toLong()))
            }

            val crypt = block.modPow(exponent, modulus)

            result.append(crypt.toByteArray().dropWhile { it.toInt() == 0 }.joinToString("") { Integer.toHexString(it.toUByte().toInt()).padStart(2, '0') })
        }

        return result.toString()
    }
}