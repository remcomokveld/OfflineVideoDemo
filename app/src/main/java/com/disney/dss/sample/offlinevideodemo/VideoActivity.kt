package com.disney.dss.sample.offlinevideodemo

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.video_activity.*

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.mainContent, VideoFragment.create(useCache = false))
                    .commitNow()
        }
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            popToMain()
            val fragment = when (menuItem.itemId) {
                R.id.cachedVideo -> VideoFragment.create(useCache = true)
                R.id.downloadVideo -> DownloadFragment()
                else -> null
            }
            fragment?.let {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.mainContent, it)
                        .addToBackStack("main")
                        .commit()
            }
            return@setOnNavigationItemSelectedListener true
        }
    }


    private fun popToMain() = supportFragmentManager.popBackStack("main", FragmentManager.POP_BACK_STACK_INCLUSIVE)
}
