package com.bitlove.fetlife.view.screen.standalone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.util.HtmlListTagHandler;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent;

public class ReleaseNotesActivity extends BaseActivity {

    public static void startActivity(Context context) {
        context.startActivity(createIntent(context));
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, ReleaseNotesActivity.class);
        intent.putExtra(EXTRA_HAS_BOTTOM_BAR,true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        TextView aboutTextView = (TextView) findViewById(R.id.text_about);

        aboutTextView.setText(Html.fromHtml(getString(R.string.text_about, getFetLifeApplication().getVersionText()), null, new HtmlListTagHandler()));

    }

    @Override
    protected void onCreateActivityComponents() {
        addActivityComponent(new MenuActivityComponent());
    }

    @Override
    protected void onSetContentView() {
        setContentView(R.layout.activity_relnotes);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
