package com.suda.yzune.wakeupschedule.schedule_import.login_school.hust

import java.math.BigInteger
import java.util.ArrayList
import kotlin.experimental.xor

class Cipher {

    fun encrypt(input: String): String {
        return strEnc(input, "1", "2", "3")
    }

/*
 * encrypt the string to string made up of hex
 * return the encrypted string
 */
    fun strEnc(data: String, firstKey: String?, secondKey: String?, thirdKey: String?): String {
        val leng = data.length
        val encData = StringBuffer()
        var firstKeyBt: ArrayList<ByteArray>? = null
        var secondKeyBt: ArrayList<ByteArray>? = null
        var thirdKeyBt: ArrayList<ByteArray>? = null
        var firstLength = 0
        var secondLength = 0
        var thirdLength = 0
        if (firstKey != null && firstKey.isNotEmpty()) {
            firstKeyBt = getKeyBytes(firstKey)
            firstLength = firstKeyBt.size
        }
        if (secondKey != null && secondKey.isNotEmpty()) {
            secondKeyBt = getKeyBytes(secondKey)
            secondLength = secondKeyBt.size
        }
        if (thirdKey != null && thirdKey.isNotEmpty()) {
            thirdKeyBt = getKeyBytes(thirdKey)
            thirdLength = thirdKeyBt.size
        }
        val iterator = leng / 4
        val remainder = leng % 4
        var i = 0
        while (i < iterator) {
            val tempData = data.substring(i * 4, i * 4 + 4)
            val tempByte = strToBt(tempData)
            var encByte = ByteArray(64)
            if (firstKey != null && !firstKey.isEmpty()) {
                var tempBt: ByteArray
                tempBt = tempByte
                for (x in 0 until firstLength) {
                    tempBt = enc(tempBt, firstKeyBt!![x])
                }
                if (secondKey != null && !secondKey.isEmpty()) {
                    for (y in 0 until secondLength) {
                        tempBt = enc(tempBt, secondKeyBt!![y])
                    }
                }
                if (thirdKey != null && !thirdKey.isEmpty()) {
                    for (z in 0 until thirdLength) {
                        tempBt = enc(tempBt, thirdKeyBt!![z])
                    }
                }
                encByte = tempBt
            }
            encData.append(bt64ToHex(encByte))
            i++
        }
        if (remainder > 0) {
            val remainderData = data.substring(iterator * 4, leng)
            val tempByte = strToBt(remainderData)
            var encByte = ByteArray(64)
            if (firstKey != null && !firstKey.isEmpty()) {
                var tempBt: ByteArray
                tempBt = tempByte
                for (x in 0 until firstLength) {
                    tempBt = enc(tempBt, firstKeyBt!![x])
                }
                if (secondKey != null && !secondKey.isEmpty()) {
                    for (y in 0 until secondLength) {
                        tempBt = enc(tempBt, secondKeyBt!![y])
                    }
                }
                if (thirdKey != null && !thirdKey.isEmpty()) {
                    for (z in 0 until thirdLength) {
                        tempBt = enc(tempBt, thirdKeyBt!![z])
                    }
                }
                encByte = tempBt
            }
            encData.append(bt64ToHex(encByte))
        }
        return encData.toString()
    }

    /*
     * chang the string into the bit array
     *
     * return bit array(it's length % 64 = 0)
     */
    private fun getKeyBytes(key: String): ArrayList<ByteArray> {
        val keyBytes = ArrayList<ByteArray>()
        val leng = key.length
        val iterator = leng / 4
        val remainder = leng % 4
        var i: Int
        i = 0
        while (i < iterator) {
            keyBytes.add(strToBt(key.substring(i * 4, i * 4 + 4)))
            i++
        }
        if (remainder > 0) {
            keyBytes.add(strToBt(key.substring(i * 4, leng)))
        }
        return keyBytes
    }

    /*
     * chang the string(it's length <= 4) into the bit array
     *
     * return bit array(it's length = 64)
     */
    private fun strToBt(str: String): ByteArray {
        val leng = str.length
        val bt = ByteArray(64)
        if (leng < 4) {
            var i = 0
            var j: Int
            var p = leng
            var q: Int
            while (i < leng) {
                val k = str[i]
                j = 0
                while (j < 16) {
                    var pow = 1
                    for (m in 15 downTo j + 1) {
                        pow *= 2
                    }
                    bt[16 * i + j] = (k.toInt() / pow % 2).toByte()
                    j++
                }
                i++
            }
            while (p < 4) {
                val k = 0
                q = 0
                while (q < 16) {
                    var pow = 1
                    for (m in 15 downTo q + 1) {
                        pow *= 2
                    }
                    bt[16 * p + q] = (k / pow % 2).toByte()
                    q++
                }
                p++
            }
        } else {
            for (i in 0..3) {
                val k = str[i]
                for (j in 0..15) {
                    var pow = 1
                    for (m in 15 downTo j + 1) {
                        pow *= 2
                    }
                    bt[16 * i + j] = (k.toInt() / pow % 2).toByte()
                }
            }
        }
        return bt
    }

    /*
     * chang the bit(it's length = 4) into the hex
     *
     * return hex
     */
    private fun bt4ToHex(binary: String): String {
        var hex = ""
        when (binary) {
            "0000" -> hex = "0"
            "0001" -> hex = "1"
            "0010" -> hex = "2"
            "0011" -> hex = "3"
            "0100" -> hex = "4"
            "0101" -> hex = "5"
            "0110" -> hex = "6"
            "0111" -> hex = "7"
            "1000" -> hex = "8"
            "1001" -> hex = "9"
            "1010" -> hex = "A"
            "1011" -> hex = "B"
            "1100" -> hex = "C"
            "1101" -> hex = "D"
            "1110" -> hex = "E"
            "1111" -> hex = "F"
        }
        return hex
    }

    /*
     * chang the hex into the bit(it's length = 4)
     *
     * return the bit(it's length = 4)
     */
    private fun hexToBt4(hex: String): String {
        var binary = ""
        when (hex) {
            "0" -> binary = "0000"
            "1" -> binary = "0001"
            "2" -> binary = "0010"
            "3" -> binary = "0011"
            "4" -> binary = "0100"
            "5" -> binary = "0101"
            "6" -> binary = "0110"
            "7" -> binary = "0111"
            "8" -> binary = "1000"
            "9" -> binary = "1001"
            "A" -> binary = "1010"
            "B" -> binary = "1011"
            "C" -> binary = "1100"
            "D" -> binary = "1101"
            "E" -> binary = "1110"
            "F" -> binary = "1111"
        }
        return binary
    }

    /*
     * chang the bit(it's length = 64) into the string
     *
     * return string
     */
    private fun byteToString(byteData: ByteArray): String {
        val str = StringBuffer()
        for (i in 0..3) {
            var count = 0
            for (j in 0..15) {
                var pow = 1
                for (m in 15 downTo j + 1) {
                    pow *= 2
                }
                count += byteData[16 * i + j] * pow
            }
            if (count != 0) {
                str.append(count.toChar())
            }
        }
        return str.toString()
    }

    private fun bt64ToHex(byteData: ByteArray): String {
        val hex = StringBuffer()
        for (i in 0..15) {
            val bt = StringBuffer()
            for (j in 0..3) {
                bt.append(byteData[i * 4 + j])
            }
            hex.append(bt4ToHex(bt.toString()))
        }
        return hex.toString()
    }

    private fun hexToBt64(hex: String): String {
        val binary = StringBuffer()
        for (i in 0..15) {
            binary.append(hexToBt4(hex.substring(i, i + 1)))
        }
        return binary.toString()
    }

    /*
     * the 64 bit des core arithmetic
     */
    private fun enc(dataByte: ByteArray, keyByte: ByteArray): ByteArray {
        val keys = generateKeys(keyByte)
        val ipByte = initPermute(dataByte)
        val ipLeft = ByteArray(32)
        val ipRight = ByteArray(32)
        val tempLeft = ByteArray(32)
        var i = 0
        var j: Int
        var k = 0
        var m: Int
        var n: Int
        while (k < 32) {
            ipLeft[k] = ipByte[k]
            ipRight[k] = ipByte[32 + k]
            k++
        }

        while (i < 16) {
            j = 0
            while (j < 32) {
                tempLeft[j] = ipLeft[j]
                ipLeft[j] = ipRight[j]
                j++
            }
            val key = ByteArray(48)
            m = 0
            while (m < 48) {
                key[m] = keys[i]!![m]
                m++
            }
            val tempRight = xor(pPermute(sBoxPermute(xor(expandPermute(ipRight), key))), tempLeft)
            n = 0
            while (n < 32) {
                ipRight[n] = tempRight[n]
                n++
            }
            i++
        }
        val finalData = ByteArray(64)
        i = 0
        while (i < 32) {
            finalData[i] = ipRight[i]
            finalData[32 + i] = ipLeft[i]
            i++
        }
        return finallyPermute(finalData)
    }

    private fun dec(dataByte: ByteArray, keyByte: ByteArray): ByteArray {
        val keys = generateKeys(keyByte)
        val ipByte = initPermute(dataByte)
        val ipLeft = ByteArray(32)
        val ipRight = ByteArray(32)
        val tempLeft = ByteArray(32)
        var i: Int
        var j: Int
        var k: Int
        var m: Int
        var n: Int
        k = 0
        while (k < 32) {
            ipLeft[k] = ipByte[k]
            ipRight[k] = ipByte[32 + k]
            k++
        }
        i = 15
        while (i >= 0) {
            j = 0
            while (j < 32) {
                tempLeft[j] = ipLeft[j]
                ipLeft[j] = ipRight[j]
                j++
            }
            val key = ByteArray(48)
            m = 0
            while (m < 48) {
                key[m] = keys[i]!![m]
                m++
            }
            val tempRight = xor(pPermute(sBoxPermute(xor(expandPermute(ipRight), key))), tempLeft)
            n = 0
            while (n < 32) {
                ipRight[n] = tempRight[n]
                n++
            }
            i--
        }
        val finalData = ByteArray(64)
        i = 0
        while (i < 32) {
            finalData[i] = ipRight[i]
            finalData[32 + i] = ipLeft[i]
            i++
        }
        return finallyPermute(finalData)
    }

    private fun initPermute(originalData: ByteArray): ByteArray {
        val ipByte = ByteArray(64)
        var i = 0
        var m = 1
        var n = 0
        while (i < 4) {
            var j = 7
            var k = 0
            while (j >= 0) {
                ipByte[i * 8 + k] = originalData[j * 8 + m]
                ipByte[i * 8 + k + 32] = originalData[j * 8 + n]
                j--
                k++
            }
            i++
            m += 2
            n += 2
        }
        return ipByte
    }

    private fun expandPermute(rightData: ByteArray): ByteArray {
        val epByte = ByteArray(48)
        for (i in 0..7) {
            if (i == 0) {
                epByte[i * 6 + 0] = rightData[31]
            } else {
                epByte[i * 6 + 0] = rightData[i * 4 - 1]
            }
            epByte[i * 6 + 1] = rightData[i * 4 + 0]
            epByte[i * 6 + 2] = rightData[i * 4 + 1]
            epByte[i * 6 + 3] = rightData[i * 4 + 2]
            epByte[i * 6 + 4] = rightData[i * 4 + 3]
            if (i == 7) {
                epByte[i * 6 + 5] = rightData[0]
            } else {
                epByte[i * 6 + 5] = rightData[i * 4 + 4]
            }
        }
        return epByte
    }

    private fun xor(byteOne: ByteArray, byteTwo: ByteArray): ByteArray {
        val xorByte = ByteArray(byteOne.size)
        for (i in byteOne.indices) {
            xorByte[i] = (byteOne[i] xor byteTwo[i])
        }
        return xorByte
    }

    private fun sBoxPermute(expandByte: ByteArray): ByteArray {
        val sBoxByte = ByteArray(32)
        var binary = ""
        val s1 = arrayOf(byteArrayOf(14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7), byteArrayOf(0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8), byteArrayOf(4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0), byteArrayOf(15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13))
        /* Table - s2 */
        val s2 = arrayOf(byteArrayOf(15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10), byteArrayOf(3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5), byteArrayOf(0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15), byteArrayOf(13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9))
        /* Table - s3 */
        val s3 = arrayOf(byteArrayOf(10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8), byteArrayOf(13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1), byteArrayOf(13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7), byteArrayOf(1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12))
        /* Table - s4 */
        val s4 = arrayOf(byteArrayOf(7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15), byteArrayOf(13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9), byteArrayOf(10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4), byteArrayOf(3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14))
        /* Table - s5 */
        val s5 = arrayOf(byteArrayOf(2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9), byteArrayOf(14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6), byteArrayOf(4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14), byteArrayOf(11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3))
        /* Table - s6 */
        val s6 = arrayOf(byteArrayOf(12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11), byteArrayOf(10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8), byteArrayOf(9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6), byteArrayOf(4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13))
        /* Table - s7 */
        val s7 = arrayOf(byteArrayOf(4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1), byteArrayOf(13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6), byteArrayOf(1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2), byteArrayOf(6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12))
        /* Table - s8 */
        val s8 = arrayOf(byteArrayOf(13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7), byteArrayOf(1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2), byteArrayOf(7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8), byteArrayOf(2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11))
        for (m in 0..7) {
            val i = expandByte[m * 6 + 0] * 2 + expandByte[m * 6 + 5]
            val j = expandByte[m * 6 + 1] * 2 * 2 * 2 + expandByte[m * 6 + 2] * 2 * 2 + expandByte[m * 6 + 3] * 2 +
                    expandByte[m * 6 + 4]
            when (m) {
                0 -> binary = getBoxBinary(s1[i][j])
                1 -> binary = getBoxBinary(s2[i][j])
                2 -> binary = getBoxBinary(s3[i][j])
                3 -> binary = getBoxBinary(s4[i][j])
                4 -> binary = getBoxBinary(s5[i][j])
                5 -> binary = getBoxBinary(s6[i][j])
                6 -> binary = getBoxBinary(s7[i][j])
                7 -> binary = getBoxBinary(s8[i][j])
            }
            sBoxByte[m * 4 + 0] = binary.substring(0, 1).toByte()
            sBoxByte[m * 4 + 1] = binary.substring(1, 2).toByte()
            sBoxByte[m * 4 + 2] = binary.substring(2, 3).toByte()
            sBoxByte[m * 4 + 3] = binary.substring(3, 4).toByte()
        }
        return sBoxByte
    }

    private fun pPermute(sBoxByte: ByteArray): ByteArray {
        val pBoxPermute = ByteArray(32)
        pBoxPermute[0] = sBoxByte[15]
        pBoxPermute[1] = sBoxByte[6]
        pBoxPermute[2] = sBoxByte[19]
        pBoxPermute[3] = sBoxByte[20]
        pBoxPermute[4] = sBoxByte[28]
        pBoxPermute[5] = sBoxByte[11]
        pBoxPermute[6] = sBoxByte[27]
        pBoxPermute[7] = sBoxByte[16]
        pBoxPermute[8] = sBoxByte[0]
        pBoxPermute[9] = sBoxByte[14]
        pBoxPermute[10] = sBoxByte[22]
        pBoxPermute[11] = sBoxByte[25]
        pBoxPermute[12] = sBoxByte[4]
        pBoxPermute[13] = sBoxByte[17]
        pBoxPermute[14] = sBoxByte[30]
        pBoxPermute[15] = sBoxByte[9]
        pBoxPermute[16] = sBoxByte[1]
        pBoxPermute[17] = sBoxByte[7]
        pBoxPermute[18] = sBoxByte[23]
        pBoxPermute[19] = sBoxByte[13]
        pBoxPermute[20] = sBoxByte[31]
        pBoxPermute[21] = sBoxByte[26]
        pBoxPermute[22] = sBoxByte[2]
        pBoxPermute[23] = sBoxByte[8]
        pBoxPermute[24] = sBoxByte[18]
        pBoxPermute[25] = sBoxByte[12]
        pBoxPermute[26] = sBoxByte[29]
        pBoxPermute[27] = sBoxByte[5]
        pBoxPermute[28] = sBoxByte[21]
        pBoxPermute[29] = sBoxByte[10]
        pBoxPermute[30] = sBoxByte[3]
        pBoxPermute[31] = sBoxByte[24]
        return pBoxPermute
    }

    private fun finallyPermute(endByte: ByteArray): ByteArray {
        val fpByte = ByteArray(64)
        fpByte[0] = endByte[39]
        fpByte[1] = endByte[7]
        fpByte[2] = endByte[47]
        fpByte[3] = endByte[15]
        fpByte[4] = endByte[55]
        fpByte[5] = endByte[23]
        fpByte[6] = endByte[63]
        fpByte[7] = endByte[31]
        fpByte[8] = endByte[38]
        fpByte[9] = endByte[6]
        fpByte[10] = endByte[46]
        fpByte[11] = endByte[14]
        fpByte[12] = endByte[54]
        fpByte[13] = endByte[22]
        fpByte[14] = endByte[62]
        fpByte[15] = endByte[30]
        fpByte[16] = endByte[37]
        fpByte[17] = endByte[5]
        fpByte[18] = endByte[45]
        fpByte[19] = endByte[13]
        fpByte[20] = endByte[53]
        fpByte[21] = endByte[21]
        fpByte[22] = endByte[61]
        fpByte[23] = endByte[29]
        fpByte[24] = endByte[36]
        fpByte[25] = endByte[4]
        fpByte[26] = endByte[44]
        fpByte[27] = endByte[12]
        fpByte[28] = endByte[52]
        fpByte[29] = endByte[20]
        fpByte[30] = endByte[60]
        fpByte[31] = endByte[28]
        fpByte[32] = endByte[35]
        fpByte[33] = endByte[3]
        fpByte[34] = endByte[43]
        fpByte[35] = endByte[11]
        fpByte[36] = endByte[51]
        fpByte[37] = endByte[19]
        fpByte[38] = endByte[59]
        fpByte[39] = endByte[27]
        fpByte[40] = endByte[34]
        fpByte[41] = endByte[2]
        fpByte[42] = endByte[42]
        fpByte[43] = endByte[10]
        fpByte[44] = endByte[50]
        fpByte[45] = endByte[18]
        fpByte[46] = endByte[58]
        fpByte[47] = endByte[26]
        fpByte[48] = endByte[33]
        fpByte[49] = endByte[1]
        fpByte[50] = endByte[41]
        fpByte[51] = endByte[9]
        fpByte[52] = endByte[49]
        fpByte[53] = endByte[17]
        fpByte[54] = endByte[57]
        fpByte[55] = endByte[25]
        fpByte[56] = endByte[32]
        fpByte[57] = endByte[0]
        fpByte[58] = endByte[40]
        fpByte[59] = endByte[8]
        fpByte[60] = endByte[48]
        fpByte[61] = endByte[16]
        fpByte[62] = endByte[56]
        fpByte[63] = endByte[24]
        return fpByte
    }

    private fun getBoxBinary(i: Byte): String {
        var binary = ""
        when (i.toInt()) {
            0 -> binary = "0000"
            1 -> binary = "0001"
            2 -> binary = "0010"
            3 -> binary = "0011"
            4 -> binary = "0100"
            5 -> binary = "0101"
            6 -> binary = "0110"
            7 -> binary = "0111"
            8 -> binary = "1000"
            9 -> binary = "1001"
            10 -> binary = "1010"
            11 -> binary = "1011"
            12 -> binary = "1100"
            13 -> binary = "1101"
            14 -> binary = "1110"
            15 -> binary = "1111"
        }
        return binary
    }

    /*
     * generate 16 keys for xor
     *
     */
    private fun generateKeys(keyByte: ByteArray): Array<ByteArray?> {
        val key = ByteArray(56)
        val keys = arrayOfNulls<ByteArray>(16)
        for (i in 0..15) {
            keys[i] = ByteArray(48)
        }
        val loop = byteArrayOf(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1)
        for (i in 0..6) {
            var j = 0
            var k = 7
            while (j < 8) {
                key[i * 8 + j] = keyByte[8 * k + i]
                j++
                k--
            }
        }
        for (i in 0..15) {
            var tempLeft: Byte
            var tempRight: Byte
            for (j in 0 until loop[i]) {
                tempLeft = key[0]
                tempRight = key[28]
                for (k in 0..26) {
                    key[k] = key[k + 1]
                    key[28 + k] = key[29 + k]
                }
                key[27] = tempLeft
                key[55] = tempRight
            }
            val tempKey = ByteArray(48)
            tempKey[0] = key[13]
            tempKey[1] = key[16]
            tempKey[2] = key[10]
            tempKey[3] = key[23]
            tempKey[4] = key[0]
            tempKey[5] = key[4]
            tempKey[6] = key[2]
            tempKey[7] = key[27]
            tempKey[8] = key[14]
            tempKey[9] = key[5]
            tempKey[10] = key[20]
            tempKey[11] = key[9]
            tempKey[12] = key[22]
            tempKey[13] = key[18]
            tempKey[14] = key[11]
            tempKey[15] = key[3]
            tempKey[16] = key[25]
            tempKey[17] = key[7]
            tempKey[18] = key[15]
            tempKey[19] = key[6]
            tempKey[20] = key[26]
            tempKey[21] = key[19]
            tempKey[22] = key[12]
            tempKey[23] = key[1]
            tempKey[24] = key[40]
            tempKey[25] = key[51]
            tempKey[26] = key[30]
            tempKey[27] = key[36]
            tempKey[28] = key[46]
            tempKey[29] = key[54]
            tempKey[30] = key[29]
            tempKey[31] = key[39]
            tempKey[32] = key[50]
            tempKey[33] = key[44]
            tempKey[34] = key[32]
            tempKey[35] = key[47]
            tempKey[36] = key[43]
            tempKey[37] = key[48]
            tempKey[38] = key[38]
            tempKey[39] = key[55]
            tempKey[40] = key[33]
            tempKey[41] = key[52]
            tempKey[42] = key[45]
            tempKey[43] = key[41]
            tempKey[44] = key[49]
            tempKey[45] = key[35]
            tempKey[46] = key[28]
            tempKey[47] = key[31]
            var m: Int
            when (i) {
                0 -> {
                    m = 0
                    while (m < 48) {
                        keys[0]!![m] = tempKey[m]
                        m++
                    }
                }
                1 -> {
                    m = 0
                    while (m < 48) {
                        keys[1]!![m] = tempKey[m]
                        m++
                    }
                }
                2 -> {
                    m = 0
                    while (m < 48) {
                        keys[2]!![m] = tempKey[m]
                        m++
                    }
                }
                3 -> {
                    m = 0
                    while (m < 48) {
                        keys[3]!![m] = tempKey[m]
                        m++
                    }
                }
                4 -> {
                    m = 0
                    while (m < 48) {
                        keys[4]!![m] = tempKey[m]
                        m++
                    }
                }
                5 -> {
                    m = 0
                    while (m < 48) {
                        keys[5]!![m] = tempKey[m]
                        m++
                    }
                }
                6 -> {
                    m = 0
                    while (m < 48) {
                        keys[6]!![m] = tempKey[m]
                        m++
                    }
                }
                7 -> {
                    m = 0
                    while (m < 48) {
                        keys[7]!![m] = tempKey[m]
                        m++
                    }
                }
                8 -> {
                    m = 0
                    while (m < 48) {
                        keys[8]!![m] = tempKey[m]
                        m++
                    }
                }
                9 -> {
                    m = 0
                    while (m < 48) {
                        keys[9]!![m] = tempKey[m]
                        m++
                    }
                }
                10 -> {
                    m = 0
                    while (m < 48) {
                        keys[10]!![m] = tempKey[m]
                        m++
                    }
                }
                11 -> {
                    m = 0
                    while (m < 48) {
                        keys[11]!![m] = tempKey[m]
                        m++
                    }
                }
                12 -> {
                    m = 0
                    while (m < 48) {
                        keys[12]!![m] = tempKey[m]
                        m++
                    }
                }
                13 -> {
                    m = 0
                    while (m < 48) {
                        keys[13]!![m] = tempKey[m]
                        m++
                    }
                }
                14 -> {
                    m = 0
                    while (m < 48) {
                        keys[14]!![m] = tempKey[m]
                        m++
                    }
                }
                15 -> {
                    m = 0
                    while (m < 48) {
                        keys[15]!![m] = tempKey[m]
                        m++
                    }
                }
            }
        }
        return keys
    }
}