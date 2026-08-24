package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.LevelRepository
import com.example.engine.PuzzleGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Arrow Puzzle", appName)
  }

  @Test
  fun `verify puzzle generation creates solvable levels`() {
    val level1 = LevelRepository.getLevel(1)
    assertEquals(4, level1.gridSize)
    assertTrue(level1.tiles.isNotEmpty())

    val clearTiles = PuzzleGenerator.getClearTiles(level1.tiles, level1.gridSize)
    assertTrue("A solvable puzzle must have at least one clear starting tile", clearTiles.isNotEmpty())
  }
}

