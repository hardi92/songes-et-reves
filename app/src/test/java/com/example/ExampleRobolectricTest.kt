package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Songes & Prophéties", appName)
  }

  @Test
  fun `crypto manager encrypts and decrypts payload`() {
    val sampleText = "Songe révélé sous le grand baobab près du fleuve"
    val encrypted = CryptoManager.encrypt(sampleText)
    val decrypted = CryptoManager.decrypt(encrypted)
    assertEquals(sampleText, decrypted)
  }
}
