package sksa.aa.tweaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import org.jpaste.exceptions.PasteException;
import org.jpaste.pastebin.Pastebin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sksa.aa.tweaker.AccountsChooseActivity.AccountsChooser;
import sksa.aa.tweaker.CarRemoverActivity.CarRemover;
import sksa.aa.tweaker.Utils.BottomDialog;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity {

    public static String appDirectory = new String();

    boolean suitableMethodFound;

    private static Context mContext;
    private ImageView noSpeedRestrictionsStatus;
    private ImageView taplimitstatus;
    private ImageView navstatus;
    private ImageView patchappstatus;
    private ImageView assistanimstatus;
    private ImageView batteryOutlineStatus;
    private ImageView opaqueStatus;
    private ImageView forceWideScreenStatus;
    private ImageView messagesHunStatus;
    private ImageView mediaHunStatus;
    private ImageView calendarTweakStatus;
    private ImageView btstatus;
    private ImageView mdstatus;
    private ImageView batteryWarningStatus;
    private ImageView telemetryStatus;
    private ImageView forceNoWideScreenStatus;
    private ImageView usbBitrateStatus;
    private ImageView wifiBitrateStatus;
    private ImageView userSeatTweakStatus;
    private ImageView coolwalkTweakStatus;
    private ImageView mirrorAppTweakStatus;
    private ImageView declineSmsTweakStatus;
    private TextView currentlySetHun;
    private TextView currentlySetMediaHun;
    private TextView currentlySetAgendaDays;
    private TextView currentlySetUSBSeekbar;
    private TextView currentlySetWiFiSeekbar;
    private Button rebootButton;
    private Button nospeed;
    private Button taplimitat;
    private Button newStartupTweak;
    private Button patchapps;
    private Button assistanim;
    private Button batteryoutline;
    private Button forceNoWideScreen;
    private Button statusbaropaque;
    private Button forceWideScreenButton;
    private Button messagesHunThrottling;
    private Button mediathrottlingbutton;
    private Button moreCalendarButton;
    private Button bluetoothoff;
    private Button mdbutton;
    private Button batteryWarning;
    private Button disableTelemetryButton;
    private Button tweakUSBBitrateButton;
    private Button tweakWiFiBitrateButton;
    private Button userSeatTweakButton;
    private Button coolwalkTweak;
    private Button deleteCarMode;
    private Button mirrorAppTweak;
    private Button declineSmsTweak;
    private boolean animationRun;
    private boolean multiAccountsMode, xpmode;

    ProgressDialog progress;

    SharedPreferences accountsPrefs;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();

        multiAccountsMode = false;




        accountsPrefs =  getSharedPreferences("accountsList", 0);

        final String path = getApplicationInfo().dataDir;
        appDirectory = path;
        loadStatus(path);



        if (extras != null && extras.getString("NewVersionName") != null) {

            BottomDialog bd;

            final BottomDialog builder2 = new BottomDialog.Builder(this)
                    .setTitle(R.string.new_version_available)
                    .setContent(getString(R.string.go_to_new_version, extras.getString("NewVersionName")))
                    .setPositiveBackgroundColor(R.color.colorPrimary)
                    .setPositiveText(R.string.go_to_download)
                    .setNegativeText(R.string.ignore_for_now)
                    .onPositive(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog dialog) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shmykelsa/AA-Tweaker/releases/")));
                        }
                    })
                    .onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog dialog) {

                        }
                    })
                    .setBackgroundColor(R.color.centercolor).build();

            builder2.show();
        }

        final int UserCount = extras.getInt("users");



        if (UserCount > 2 && !multiAccountsMode) {
            showManyAccountsWarning(path, UserCount);
        }



        setContentView(R.layout.activity_main);

        ImageView revertNotificationDuration = findViewById(R.id.revert_hun_throttling);
        ImageView revertMediaNotificationDuration = findViewById(R.id.revert_media_hun);
        ImageView revertCalendarDays = findViewById(R.id.revert_calendar_days);
        ImageView revertWifiBitrate = findViewById(R.id.revert_bitrate_wifi);
        ImageView revertUsbBitrate = findViewById(R.id.revert_bitrate_usb);


        ViewPager viewPager = findViewById(R.id.viewpager);
        CommonPageAdapter adapter = new CommonPageAdapter();
        adapter.insertViewId(R.id.page_one);
        adapter.insertViewId(R.id.page_two);
        viewPager.setAdapter(adapter);


        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Button toapp = findViewById(R.id.toapp_button);
        toapp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AppsList.class);
                        startActivity(intent);
                    }
                }
        );

        Button rebootbutton = findViewById(R.id.reboot_button);
        final DialogFragment rebootDialog = new RebootDialog();
        rebootbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rebootDialog.show(getSupportFragmentManager(), "RebootDialog");
                    }
                }
        );

        rebootButton = findViewById(R.id.reboot_button);


        animationRun = false;
        final TextView upperTextView = findViewById(R.id.legend);
        upperTextView.setText(R.string.main_string);
        final AlphaAnimation legendAnim;
        legendAnim = new AlphaAnimation(1.0f, 0.0f);
        legendAnim.setDuration(100);
        legendAnim.setRepeatCount(1);
        legendAnim.setRepeatMode(Animation.REVERSE);
        legendAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (upperTextView.getText().toString().equals(getString(R.string.legend))) {
                    upperTextView.setText(R.string.main_string);
                } else {
                    upperTextView.setText(R.string.legend);
                }
            }
        });

        if (UserCount == 0) {
            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.warning_title));
            alertDialog.setMessage(getString(R.string.no_accounts_warning));

            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "GITHUB", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/shmykelsa/AA-Tweaker/issues/new")));
                }
            });
            alertDialog.show();
        }

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upperTextView.startAnimation(legendAnim);
                    }
                });
            }
        }, 12000, 12000);

/*        nospeed = findViewById(R.id.nospeed);
        noSpeedRestrictionsStatus = findViewById(R.id.speedhackstatus);
        if (load("aa_speed_hack")) {
            nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
            changeStatus(noSpeedRestrictionsStatus, 2, false);
        } else {
            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
            changeStatus(noSpeedRestrictionsStatus, 0, false);
        }

        nospeed.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_speed_hack")) {
                            revert("aa_speed_hack");
                            nospeed.setText(getString(R.string.disable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            changeStatus(noSpeedRestrictionsStatus, 0, true);
                            showRebootButton();
                        } else {
                            patchforspeed(UserCount);
                        }
                    }
                });

        setOnLongClickListener(nospeed, R.string.tutorial_nospeed, R.drawable.tutorial_nospeed);*/



        taplimitat = findViewById(R.id.taplimit);
        taplimitstatus = findViewById(R.id.sixtapstatus);
        if (load("aa_six_tap")) {
            taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
            changeStatus(taplimitstatus, 2, false);

        } else {
            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
            changeStatus(taplimitstatus, 0, false);

        }

        setOnLongClickListener(taplimitat, R.string.tutorial_sixtap, R.drawable.tutorial_sixtap);


        taplimitat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_six_tap")) {
                            revert("aa_six_tap");
                            taplimitat.setText(getString(R.string.disable_tweak_string) + getString(R.string.disable_speed_limitations));
                            changeStatus(taplimitstatus, 0, true);
                            showRebootButton();
                        } else {
                            patchfortouchlimit(UserCount);
                        }
                    }
                });

        newStartupTweak = findViewById(R.id.startup);
        navstatus = findViewById(R.id.startupstatus);
        if (load("aa_new_startup")) {
            newStartupTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.custom_startup_option));
            changeStatus(navstatus, 2, false);
        } else {
            newStartupTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.custom_startup_option));
            changeStatus(navstatus, 0, false);
        }
        newStartupTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_new_startup")) {
                            revert("aa_new_startup");
                            newStartupTweak.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.custom_startup_option));
                            changeStatus(navstatus, 0, true);
                            showRebootButton();
                        } else {
                            navpatch(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(newStartupTweak, R.string.tutorial_custom_startup_option);

        patchapps = findViewById(R.id.patchapps);
        patchappstatus = findViewById(R.id.patchedappstatus);


        if (load("aa_patched_apps")) {
            patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
            changeStatus(patchappstatus, 2, false);
        } else {
            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
            changeStatus(patchappstatus, 0, false);
        }

        patchapps.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_patched_apps")) {
                            revert("aa_patched_apps");
                            patchapps.setText(getString(R.string.patch_app) + getString(R.string.patch_custom_apps));
                            changeStatus(patchappstatus, 0, true);
                            showRebootButton();
                        } else {
                            SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
                            Map<String, ?> allEntries = appsListPref.getAll();
                            if (allEntries.isEmpty()) {
                                Intent intent = new Intent(MainActivity.this, AppsList.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_apps_warning), Toast.LENGTH_LONG).show();
                            } else {
                                patchforapps(UserCount);
                            }
                        }
                    }
                });

        setOnLongClickListener(patchapps, R.string.tutorial_patchapps);


        assistanim = findViewById(R.id.assistanim);
        assistanimstatus = findViewById(R.id.assistanimstatus);
        if (load("aa_assistant_rail")) {
            assistanim.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
            changeStatus(assistanimstatus, 2, false);

        } else {
            assistanim.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
            changeStatus(assistanimstatus, 0, false);
        }

        assistanim.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_assistant_rail")) {
                            revert("aa_assistant_rail");
                            assistanim.setText(getString(R.string.enable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
                            changeStatus(assistanimstatus, 0, true);
                            showRebootButton();
                        } else {
                            patchrailassistant(UserCount);
                        }
                    }
                });

        setOnLongClickListener(assistanim, R.string.tutorial_animation, R.drawable.tutorial_animation);


        batteryoutline = findViewById(R.id.battoutline);
        batteryOutlineStatus = findViewById(R.id.batterystatus);
        if (load("aa_battery_outline")) {
            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
            changeStatus(batteryOutlineStatus, 2, false);

        } else {
            batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
            changeStatus(batteryOutlineStatus, 0, false);
        }

        batteryoutline.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_battery_outline")) {
                            revert("aa_battery_outline");
                            batteryoutline.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_outline_string));
                            changeStatus(batteryOutlineStatus, 0, true);
                            showRebootButton();
                        } else {
                            battOutline(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(batteryoutline, R.string.tutorial_battery_outline, R.drawable.tutorial_outline);

        statusbaropaque = findViewById(R.id.statusbar_opaque);
        opaqueStatus = findViewById(R.id.statusbar_opaque_status);
        if (load("aa_sb_opaque")) {
            statusbaropaque.setText(getString(R.string.disable_tweak_string) + getString(R.string.statb_opaque_string));
            changeStatus(opaqueStatus, 2, false);

        } else {
            statusbaropaque.setText(getString(R.string.enable_tweak_string) + getString(R.string.statb_opaque_string));
            changeStatus(opaqueStatus, 0, false);
        }

        statusbaropaque.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_sb_opaque")) {
                            revert("aa_sb_opaque");
                            statusbaropaque.setText(getString(R.string.enable_tweak_string) + getString(R.string.statb_opaque_string));
                            changeStatus(opaqueStatus, 0, true);
                            showRebootButton();
                        } else {
                            opaqueStatusBar(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(statusbaropaque, R.string.tutorial_statusbar_opaque);

        forceNoWideScreen = findViewById(R.id.force__no_ws_button);
        forceNoWideScreenStatus = findViewById(R.id.force_no_ws_status);

        forceWideScreenButton = findViewById(R.id.force_ws_button);
        forceWideScreenStatus = findViewById(R.id.force_ws_status);

        if (load("force_ws")) {
            forceWideScreenButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.force_widescreen_text));
            changeStatus(forceWideScreenStatus, 2, false);
        } else {
            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
            changeStatus(forceWideScreenStatus, 0, false);
        }

        forceWideScreenButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_ws")) {
                            revert("force_ws");
                            forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                            changeStatus(forceWideScreenStatus, 0, true);
                            showRebootButton();
                        } else {
                            forceWideScreen(view, 470, UserCount);
                            forceWideScreenButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.force_widescreen_text));
                            if (load("force_no_ws")) {
                                Toast.makeText(getApplicationContext(), getString(R.string.force_disable_widescreen_warning), Toast.LENGTH_LONG).show();
                                save(false, "force_no_ws");
                                forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                                changeStatus(forceNoWideScreenStatus, 0, true);
                            }
                        }
                    }
                });

        setOnLongClickListener(forceWideScreenButton, R.string.tutorial_widescreen, R.drawable.tutorial_widescreen);


        if (load("force_no_ws")) {
            forceNoWideScreen.setText(getString(R.string.reset_tweak) + getString(R.string.base_no_ws));
            changeStatus(forceNoWideScreenStatus, 2, false);

        } else {
            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
            changeStatus(forceNoWideScreenStatus, 0, false);
        }

        forceNoWideScreen.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("force_no_ws")) {
                            revert("force_no_ws");
                            forceNoWideScreen.setText(getString(R.string.force_disable_tweak) + getString(R.string.base_no_ws));
                            changeStatus(forceNoWideScreenStatus, 0, true);
                            showRebootButton();
                        } else {
                            forceWideScreen(view, 3000, UserCount);
                            forceNoWideScreen.setText(getString(R.string.reset_tweak) + getString(R.string.base_no_ws));
                            if (load("force_ws")) {
                                save(false, "force_ws");
                                Toast.makeText(getApplicationContext(), R.string.force_widescreen_warning, Toast.LENGTH_LONG).show();
                                forceWideScreenButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.force_widescreen_text));
                                changeStatus(forceWideScreenStatus, 0, true);
                            }
                        }
                    }
                });

        setOnLongClickListener(forceNoWideScreen, R.string.tutorial_no_widescreen, R.drawable.tutorial_nowidescreen);

        messagesHunThrottling = findViewById(R.id.hunthrottlingbutton);
        final int[] messagesHunScrollbarValue = {0};
        final TextView displayValue = findViewById(R.id.seekbar_text);
        final SeekBar hunSeekbar = findViewById(R.id.messages_hun_seekbar);
        hunSeekbar.setProgress(8000);
        displayValue.setText(hunSeekbar.getProgress() + "ms");
        hunSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int) Math.round(progress / 100)) * 100;
                seekBar.setProgress(progress);
                messagesHunScrollbarValue[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                if (hunSeekbar.getProgress() == 8000) {
                    messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
                } else {
                    messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress() + " ms");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress() + " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                messagesHunScrollbarValue[0] = hunSeekbar.getProgress();
                displayValue.setText(hunSeekbar.getProgress() + "ms");
                if (hunSeekbar.getProgress() == 8000) {
                    messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
                } else {
                    messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + " " + hunSeekbar.getProgress() + " ms");
                }
            }
        });

        revertNotificationDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hunSeekbar.setProgress(8000);
            }
        });


        messagesHunStatus = findViewById(R.id.huntrottlingstatus);

        currentlySetHun = findViewById(R.id.notification_currently_set);
        if (load("aa_hun_ms")) {
            messagesHunThrottling.setText(getString(R.string.reset_tweak) + getString(R.string.set_notification_duration_to) + getString(R.string.default_string));
            changeStatus(messagesHunStatus, 2, false);
            if (loadValue("messaging_hun_value") == 0) {
                saveValue(Integer.parseInt(runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT DISTINCT intVal FROM FlagOverrides WHERE name=\"SystemUi__hun_default_heads_up_timeout_ms\";'").getInputStreamLog()), "messaging_hun_value");
            }
            currentlySetHun.setText(getString(R.string.currently_set) + loadValue("messaging_hun_value"));
        } else {
            messagesHunThrottling.setText(getString(R.string.set_value) + getString(R.string.set_notification_duration_to) + "...");
            changeStatus(messagesHunStatus, 0, false);
        }



        messagesHunThrottling.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (hunSeekbar.getProgress() == 8000) {
                            if (load("aa_hun_ms")) {
                                revert("aa_hun_ms");
                                saveValue(0, "messaging_hun_value");
                                changeStatus(messagesHunStatus, 0, true);
                                currentlySetHun.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setHunDuration(view, hunSeekbar.getProgress(), UserCount);
                        }
                    }
                });

        setOnLongClickListener(messagesHunThrottling, R.string.tutorial_hun, R.drawable.tutorial_hun);

        mediathrottlingbutton = findViewById(R.id.media_throttling_button);
        final int[] secondScrollBarStatus = {0};
        final TextView secondDisplayValue = findViewById(R.id.second_seekbar_text);
        final SeekBar mediaSeekbar = findViewById(R.id.media_hun_seekbar);
        mediaSeekbar.setProgress(8000);
        secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
        mediaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = ((int) Math.round(progress / 1000)) * 1000;
                mediaSeekbar.setProgress(progress);
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                if (mediaSeekbar.getProgress() == 8000) {
                    mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
                } else {
                    mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress() + " ms");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress() + " ms");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                secondScrollBarStatus[0] = mediaSeekbar.getProgress();
                secondDisplayValue.setText(mediaSeekbar.getProgress() + "ms");
                if (mediaSeekbar.getProgress() == 8000) {
                    mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
                } else {
                    mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + " " + mediaSeekbar.getProgress() + " ms");
                }
            }
        });

        revertMediaNotificationDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaSeekbar.setProgress(8000);
            }
        });

        currentlySetMediaHun = findViewById(R.id.media_notification_currently_set);
        mediaHunStatus = findViewById(R.id.media_trhrottling_status);
        if (load("aa_media_hun")) {
            mediathrottlingbutton.setText(getString(R.string.reset_tweak) + getString(R.string.media_notification_duration_to) + getString(R.string.default_string));
            changeStatus(mediaHunStatus, 2, false);
            if (loadValue("media_hun_value") == 0) {
                saveValue(Integer.parseInt(runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT DISTINCT intVal FROM FlagOverrides WHERE name=\"SystemUi__media_hun_in_rail_widget_timeout_ms\";'").getInputStreamLog()), "media_hun_value");
            }
            currentlySetMediaHun.setText(getString(R.string.currently_set) + loadValue("media_hun_value"));
        } else {
            mediathrottlingbutton.setText(getString(R.string.set_value) + getString(R.string.media_notification_duration_to) + "...");
            changeStatus(mediaHunStatus, 0, false);
        }

        mediathrottlingbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_media_hun")) {
                            if (mediaSeekbar.getProgress() == 8000) {
                                revert("aa_media_hun");
                                saveValue(0, "media_hun_value");
                                changeStatus(mediaHunStatus, 0, true);
                                currentlySetMediaHun.setText("");
                            } else {
                                setMediaHunDuration(view, mediaSeekbar.getProgress(), UserCount);
                            }
                            showRebootButton();
                        } else {
                            setMediaHunDuration(view, mediaSeekbar.getProgress(), UserCount);
                        }
                    }
                });

        setOnLongClickListener(mediathrottlingbutton, R.string.tutorial_media_hun, R.drawable.tutorial_media_hun);

        moreCalendarButton = findViewById(R.id.calendar_more_events_button);
        final int[] calendarSeekbarStatus = {0};
        final TextView calendarSeekbarTextView = findViewById(R.id.calendar_days_seekbar_text);
        final SeekBar calendarSeekbar = findViewById(R.id.calendar_days_seekbar);
        calendarSeekbar.setProgress(1);
        calendarSeekbarTextView.setText("1");
        calendarSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                calendarSeekbar.setProgress(progress);
                calendarSeekbarTextView.setText(calendarSeekbar.getProgress() + "");
                if (progress == 1 || progress == 0) {
                    moreCalendarButton.setText(getString(R.string.calendar_tweak_single, calendarSeekbar.getProgress()));
                } else {
                    moreCalendarButton.setText(getString(R.string.calendar_tweak, calendarSeekbar.getProgress()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                calendarSeekbarTextView.setText(calendarSeekbar.getProgress() + "");
                moreCalendarButton.setText(getString(R.string.calendar_tweak, calendarSeekbar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                calendarSeekbarStatus[0] = calendarSeekbar.getProgress();
                calendarSeekbarTextView.setText(calendarSeekbar.getProgress() + "");
            }
        });

        revertCalendarDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarSeekbar.setProgress(1);
            }
        });


        currentlySetAgendaDays = findViewById(R.id.calendar_days_currently_set);
        calendarTweakStatus = findViewById(R.id.calendar_more_events_status);

        if (load("calendar_aa_tweak")) {
            moreCalendarButton.setText(getString(R.string.calendar_tweak_single, calendarSeekbar.getProgress()));
            changeStatus(calendarTweakStatus, 2, false);
            if (loadValue("agenda_value") == 0) {
                saveValue(Integer.parseInt(runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT DISTINCT intVal FROM FlagOverrides WHERE name=\"McFly__num_days_in_agenda_view\";'").getInputStreamLog()), "agenda_value");
            }
            currentlySetAgendaDays.setText(getString(R.string.currently_set) + loadValue("agenda_value"));
        } else {
            moreCalendarButton.setText(getString(R.string.calendar_tweak_single, calendarSeekbar.getProgress()));
            changeStatus(calendarTweakStatus, 0, false);
        }

        moreCalendarButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (calendarSeekbar.getProgress() == 1) {
                            if (load("calendar_aa_tweak")) {
                                revert("calendar_aa_tweak");
                                saveValue(1, "agenda_value");
                                changeStatus(calendarTweakStatus, 0, true);
                                currentlySetAgendaDays.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setCalendarEvents(view, calendarSeekbar.getProgress(), UserCount);
                        }
                    }
                });

        setOnLongClickListener(moreCalendarButton, R.string.tutorial_calendar_tweak, R.drawable.tutorial_agenda);

        bluetoothoff = findViewById(R.id.bluetooth_disable_button);
        btstatus = findViewById(R.id.bt_disable_status);
        if (load("bluetooth_pairing_off")) {
            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            changeStatus(btstatus, 2, false);
        } else {
            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
            changeStatus(btstatus, 0, false);

        }

        bluetoothoff.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("bluetooth_pairing_off")) {
                            revert("bluetooth_pairing_off");
                            bluetoothoff.setText(getString(R.string.disable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                            changeStatus(btstatus, 0, true);
                            showRebootButton();
                        } else {
                            forceNoBt(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(bluetoothoff, R.string.tutorial_bluetooth);


        mdbutton = findViewById(R.id.multi_display_button);
        mdstatus = findViewById(R.id.multi_display_status);
        if (load("multi_display")) {
            mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));
            changeStatus(mdstatus, 2, false);
        } else {
            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
            changeStatus(mdstatus, 0, false);
        }

        mdbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("multi_display")) {
                            revert("multi_display");
                            mdbutton.setText(getString(R.string.enable_tweak_string) + getString(R.string.multi_display_string));
                            changeStatus(mdstatus, 0, true);
                            showRebootButton();
                        } else {
                            multiDisplay(UserCount);
                        }
                    }
                });

        setOnLongClickListener(mdbutton, R.string.tutorial_multidisplay, R.drawable.tutorial_md1, R.drawable.tutorial_md2, R.drawable.tutorial_md3);

        batteryWarning = findViewById(R.id.battery_warning_button);
        batteryWarningStatus = findViewById(R.id.battery_warning_status);
        if (load("battery_saver_warning")) {
            batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
            changeStatus(batteryWarningStatus, 2, false);
        } else {
            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
            changeStatus(batteryWarningStatus, 0, false);
        }

        batteryWarning.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("battery_saver_warning")) {
                            revert("battery_saver_warning");
                            batteryWarning.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_warning));
                            changeStatus(batteryWarningStatus, 0, true);
                            showRebootButton();
                        } else {
                            disableBatteryWarning(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(batteryWarning, R.string.tutorial_battery_saver_warning, R.drawable.tutorial_battery_saver);


        disableTelemetryButton = findViewById(R.id.telemetry_disable_tweak);
        telemetryStatus = findViewById(R.id.telemetry_disable_status);
        if (load("kill_telemetry")) {
            disableTelemetryButton.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.telemetry_string));
            changeStatus(telemetryStatus, 2, false);
        } else {
            disableTelemetryButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.telemetry_string));
            changeStatus(telemetryStatus, 0, false);
        }

        disableTelemetryButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("kill_telemetry")) {
                            revert("kill_telemetry");
                            disableTelemetryButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.telemetry_string));
                            changeStatus(telemetryStatus, 0, true);
                            showRebootButton();
                        } else {
                            disableTelemetry(view, UserCount);

                        }
                    }
                });

        setOnLongClickListener(disableTelemetryButton, R.string.tutorial_telemetry);

        tweakUSBBitrateButton = findViewById(R.id.tweak_bitrate_usb);
        final int[] usbBitrateValue = {0};
        final TextView currentSeekbarUSB = findViewById(R.id.usb_bitrate_currently_set);
        final TextView toBeSetSeekbarUSB = findViewById(R.id.usb_bitrate_to_be_set);
        final SeekBar usbBitrateSeekbar = findViewById(R.id.usb_bitrate_seekbar);
        final Double[] valueUSB = new Double[1];
        usbBitrateSeekbar.setProgress(10);
        toBeSetSeekbarUSB.setText("1.0" + "X");
        usbBitrateSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueUSB[0] = (Double.valueOf(progress) / 10.0);
                toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                if (usbBitrateSeekbar.getProgress() == 10) {
                    tweakUSBBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_usb_bitrate) + " " + getString(R.string.default_string));
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                } else {
                    tweakUSBBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_usb_bitrate) + " " + valueUSB[0] + " X");
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (usbBitrateSeekbar.getProgress() == 10) {
                    tweakUSBBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_usb_bitrate) + " " + getString(R.string.default_string));
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                } else {
                    tweakUSBBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_usb_bitrate) + " " + valueUSB[0] + " X");
                    toBeSetSeekbarUSB.setText(valueUSB[0] + "X");
                }
            }
        });

        revertUsbBitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usbBitrateSeekbar.setProgress(10);
            }
        });


        usbBitrateStatus = findViewById(R.id.tweak_bitrate_usb_status);

        currentlySetUSBSeekbar = findViewById(R.id.usb_bitrate_currently_set);
        if (load("aa_bitrate_usb")) {
            tweakUSBBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_usb_bitrate) + " " + getString(R.string.default_string));
            changeStatus(usbBitrateStatus, 2, false);
            currentlySetUSBSeekbar.setText(getString(R.string.currently_set) + loadFloat("usb_bitrate_value"));
        } else {
            tweakUSBBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_usb_bitrate) + "...");
            changeStatus(usbBitrateStatus, 0, false);
        }

        tweakUSBBitrateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (usbBitrateSeekbar.getProgress() == 10) {
                            if (load("aa_bitrate_usb")) {
                                revert("aa_bitrate_usb");
                                saveFloat(0, "usb_bitrate_value");
                                changeStatus(usbBitrateStatus, 0, true);
                                currentlySetUSBSeekbar.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setUSBbitrate(valueUSB[0], UserCount);

                        }
                    }
                });

        setOnLongClickListener(tweakUSBBitrateButton, R.string.tutorial_bitrate);




        tweakWiFiBitrateButton = findViewById(R.id.tweak_bitrate_wifi);
        final int[] wifiBitrateValue = {0};
        final TextView currentSeekbarWiFi = findViewById(R.id.wifi_bitrate_currently_set);
        final TextView toBeSetSeekbarWiFi = findViewById(R.id.wifi_bitrate_to_be_set);
        final SeekBar WiFiBitrateSeekbar = findViewById(R.id.wifi_bitrate_seekbar);
        final Double[] valueWiFi = new Double[1];
        WiFiBitrateSeekbar.setProgress(10);
        toBeSetSeekbarWiFi.setText("1.0" + "X");
        WiFiBitrateSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueWiFi[0] = (Double.valueOf(progress) / 10.0);
                toBeSetSeekbarWiFi.setText(valueWiFi[0] + "X");
                if (WiFiBitrateSeekbar.getProgress() == 10) {
                    tweakWiFiBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_wifi_tweak) + " " + getString(R.string.default_string));
                } else {
                    tweakWiFiBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_wifi_tweak) + " " + valueWiFi[0] + " X");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (WiFiBitrateSeekbar.getProgress() == 10) {
                    tweakWiFiBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_wifi_tweak) + " " + getString(R.string.default_string));
                } else {
                    tweakWiFiBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_wifi_tweak) + " " + valueWiFi[0] + " X");
                }
            }
        });

        setOnLongClickListener(tweakWiFiBitrateButton, R.string.tutorial_bitrate);

        revertWifiBitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WiFiBitrateSeekbar.setProgress(10);
            }
        });


        wifiBitrateStatus = findViewById(R.id.tweak_bitrate_wifi_status);
        currentlySetWiFiSeekbar = findViewById(R.id.wifi_bitrate_currently_set);
        if (load("aa_bitrate_wifi")) {
            tweakWiFiBitrateButton.setText(getString(R.string.reset_tweak) + getString(R.string.set_wifi_tweak) + " " + getString(R.string.default_string));
            changeStatus(wifiBitrateStatus, 2, false);
            currentlySetWiFiSeekbar.setText(getString(R.string.currently_set) + loadFloat("wifi_bitrate_value"));
        } else {
            tweakWiFiBitrateButton.setText(getString(R.string.set_value) + getString(R.string.set_wifi_tweak) + "...");
            changeStatus(wifiBitrateStatus, 0, false);
        }

        tweakWiFiBitrateButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (WiFiBitrateSeekbar.getProgress() == 10) {
                            if (load("aa_bitrate_wifi")) {
                                revert("aa_bitrate_wifi");
                                saveFloat(0, "wifi_bitrate_value");
                                changeStatus(wifiBitrateStatus, 0, true);
                                currentlySetWiFiSeekbar.setText("");
                                showRebootButton();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.choose_value_first), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setWiFiBitrate(valueWiFi[0], UserCount);

                        }
                    }
                });



        userSeatTweakButton = findViewById(R.id.userseat_tweak_button);
        userSeatTweakStatus = findViewById(R.id.userseat_tweak_status);


        if (load("aa_userseat_tweak")) {
            userSeatTweakButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.custom_hotseat_option));
            changeStatus(userSeatTweakStatus, 2, false);
        } else {
            userSeatTweakButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.custom_hotseat_option));
            changeStatus(userSeatTweakStatus, 0, false);
        }

        userSeatTweakButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_userseat_tweak")) {
                            revert("aa_userseat_tweak");
                            userSeatTweakButton.setText(getString(R.string.enable_tweak_string) + getString(R.string.custom_hotseat_option));
                            changeStatus(userSeatTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            userSeatTogglePatch(UserCount);
                        }
                    }
                });

        setOnLongClickListener(userSeatTweakButton, R.string.tutorial_darkmodeswitch, R.drawable.tutorial_darkswitch1, R.drawable.tutorial_darkswitch2, R.drawable.tutorial_darkswitch3);

        coolwalkTweak = findViewById(R.id.coolwalk_tweak_button);
        coolwalkTweakStatus = findViewById(R.id.coolwalk_tweak_status);


        if (load("aa_activate_coolwalk")) {
            coolwalkTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_tweak));
            changeStatus(coolwalkTweakStatus, 2, false);
        } else {
            coolwalkTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_tweak));
            changeStatus(coolwalkTweakStatus, 0, false);
        }

        coolwalkTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_activate_coolwalk")) {
                            revert("aa_activate_coolwalk");
                            coolwalkTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.coolwalk_tweak));
                            changeStatus(coolwalkTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            activateCoolwalk(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(coolwalkTweak, R.string.tutorial_coolwalk, R.drawable.tutorial_darkswitch2, R.drawable.tutorial_darkswitch3);


        mirrorAppTweak = findViewById(R.id.mirrorapp_tweak_button);
        mirrorAppTweakStatus = findViewById(R.id.mirrorapp_tweak_status);


        if (load("aa_activate_mirrorapp")) {
            mirrorAppTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.casting_app));
            changeStatus(mirrorAppTweakStatus, 2, false);
        } else {
            mirrorAppTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.casting_app));
            changeStatus(mirrorAppTweakStatus, 0, false);
        }

        mirrorAppTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_activate_mirrorapp")) {
                            revert("aa_activate_mirrorapp");
                            mirrorAppTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.casting_app));
                            changeStatus(mirrorAppTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            activateMirroringApp(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(mirrorAppTweak, R.string.tutorial_mirroring_app);

        declineSmsTweak = findViewById(R.id.declinesms_tweak_button);
        declineSmsTweakStatus = findViewById(R.id.declinesms_tweak_status);


        if (load("aa_activate_mirrorapp")) {
            declineSmsTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.decline_message_tweak));
            changeStatus(declineSmsTweakStatus, 2, false);
        } else {
            declineSmsTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.decline_message_tweak));
            changeStatus(declineSmsTweakStatus, 0, false);
        }

        declineSmsTweak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (load("aa_activate_mirrorapp")) {
                            revert("aa_activate_mirrorapp");
                            declineSmsTweak.setText(getString(R.string.enable_tweak_string) + getString(R.string.decline_message_tweak));
                            changeStatus(declineSmsTweakStatus, 0, true);
                            showRebootButton();
                        } else {
                            activatesmsdecline(view, UserCount);
                        }
                    }
                });

        setOnLongClickListener(declineSmsTweak, R.string.tutorial_decline_message);




        deleteCarMode = findViewById(R.id.car_remover);
        deleteCarMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CarRemover.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });

        deleteCarMode.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);

                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(R.string.tutorial_carremover));

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, WRAP_CONTENT);

                return true;
            }
        });

    }



    private void setOnLongClickListener(final Button button, final int... p) {
        button.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);

                TextView tutorial = view.findViewById(R.id.dialog_content);
                tutorial.setText(getString(p[0]));

                ImageView img1 = view.findViewById(R.id.tutorialimage1);

                if (p.length>1) {
                    img1.setImageDrawable(getDrawable(p[1]));
                }

                ImageView img2 = view.findViewById(R.id.tutorialimage2);
                if (p.length>2) {
                    img2.setImageDrawable(getDrawable(p[2]));
                }

                ImageView img3 = view.findViewById(R.id.tutorialimage3);
                if (p.length>3) {
                    img3.setImageDrawable(getDrawable(p[3]));
                }

                dialog.setContentView(view);

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(ViewPager.LayoutParams.MATCH_PARENT, WRAP_CONTENT);

                return true;
            }
        });
    }

    private void showManyAccountsWarning(final String path, int userCount) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.warning_title));
        builder.setMessage(getResources().getString(R.string.many_accounts_warning, userCount, userCount));
        builder.setNeutralButton( getString(R.string.use_all),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        multiAccountsMode = true;
                        xpmode = true;
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton( R.string.choose_accounts
                ,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(MainActivity.this, AccountsChooser.class);
                        intent.putExtra("path", path);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }


    private void revert(final String toRevert) {

        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;

                save(false, toRevert);

                appendText(logs, "\n\n-- Reverting the hack  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS " + toRevert + ";'\n"
                ).getStreamLogsWithLabels());
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DELETE FROM FlagOverrides;'\n" //make sure a good clean is done after dropping the trigger
                ).getStreamLogsWithLabels());
            }


        }.start();


    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.version);
        item.setTitle("V." + BuildConfig.VERSION_NAME);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy:

                final String title = "log";
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                final URL[] string = {null};
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            TextView textView = findViewById(R.id.logs);
                            URL newstring = Pastebin.pastePaste(BuildConfig.PASTEBIN_API_KEY, String.valueOf(textView.getText()), title);
                            Toast.makeText(getApplicationContext(), getString(R.string.copied_pastebin), Toast.LENGTH_LONG).show();
                            ClipData clip = ClipData.newPlainText("logs", newstring.toString());
                            clipboard.setPrimaryClip(clip);
                        } catch (PasteException e) {
                            e.printStackTrace();
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            TextView textView = findViewById(R.id.logs);
                            Toast.makeText(getApplicationContext(), getString(R.string.log_copied), Toast.LENGTH_LONG).show();
                            ClipData clip = ClipData.newPlainText("logs", textView.getText());
                            clipboard.setPrimaryClip(clip);
                        } catch (RuntimeException e) {
                            e.printStackTrace();
                            final ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);

                            Toast.makeText(getApplicationContext(), getString(R.string.log_copied), Toast.LENGTH_LONG).show();
                            Toast.makeText(getApplicationContext(), getString(R.string.log_copied), Toast.LENGTH_LONG).show();
                            TextView textView = findViewById(R.id.logs);

                            ClipData clip = ClipData.newPlainText("logs", textView.getText());
                            clipboard.setPrimaryClip(clip);
                        }
                    }
                });




                break;

            case R.id.about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getSupportFragmentManager(), "AboutDialog");
                break;

            case R.id.revert_everything:
                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                builder.setMessage(getString(R.string.revert_everything_dialog))
                        .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getAndRemoveOptionsSelected();
                            }
                        })
                        .setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.setCancelable(true);
                android.support.v7.app.AlertDialog Alert1 = builder.create();
                Alert1.show();
                break;
            case R.id.aa_settings:
                String packageName = "com.google.android.projection.gearhead";
                openApp(getApplicationContext(), packageName);

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    public void save(final boolean isChecked, String key) {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, isChecked);
        editor.apply();
    }

    public void saveValue(final int value, String key) {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void saveFloat(final float value, String key) {
        SharedPreferences sharedPreferences = getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public boolean load(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public int loadValue(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public float loadFloat(String key) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void patchforapps(int usercount) {
        final TextView logs = findViewById(R.id.logs);

        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);


        SharedPreferences appsListPref = getApplicationContext().getSharedPreferences("appsListPref", 0);
        Map<String, ?> allEntries = appsListPref.getAll();
        logs.append("--  Apps which will be added to whitelist: --\n");
        String whiteListString = "";
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            logs.append("\t\t- " + entry.getValue() + " (" + entry.getKey() + ")\n");
            whiteListString += "," + entry.getKey();

            String pathResult = runSuWithCmd("pm path " + entry.getKey()).getInputStreamLogWithLabel();
            String actualPath = pathResult.substring(pathResult.lastIndexOf(":") + 1);

            appendText(logs , runSuWithCmd("mv " + actualPath + " /data/local/tmp/tmpapk" + entry.getKey() + ".apk").getStreamLogsWithLabels());
            appendText(logs , runSuWithCmd("pm uninstall " + entry.getKey()).getStreamLogsWithLabels());
            appendText(logs, runSuWithCmd("pm install -i \"com.android.vending\" -r" + " /data/local/tmp/tmpapk" + entry.getKey() + ".apk" ).getStreamLogsWithLabels());


        }

        whiteListString = whiteListString.replaceFirst(",", "");
        final String whiteListStringFinal = whiteListString;
        final StringBuilder finalCommand = new StringBuilder();



        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, stringVal, committed) VALUES (\"com.google.android.gms.car\",0,\"app_white_list\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1),\"");
            finalCommand.append(whiteListStringFinal);
            finalCommand.append("\",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, stringVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"AppValidation__allowed_package_list\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1),\"");
            finalCommand.append(whiteListStringFinal);
            finalCommand.append("\",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"AppValidation__should_bypass_validation\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"AppValidation__play_install_api\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("DELETE FROM Flags WHERE name=\"AppValidation__blocked_packages\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"AppValidation__allowed_package_list\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"AppValidation__should_bypass_validation\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"app_white_list\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"app_black_list\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"AppValidation__blocked_packages_by_installer\";");
            finalCommand.append(System.getProperty("line.separator"));

        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }


                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DELETE FROM Flags WHERE name=\"app_white_list\";\n" +
                                "DROP TRIGGER IF EXISTS aa_patched_apps;\n DROP TRIGGER IF EXISTS after_delete;\n" +
                                "DROP TRIGGER IF EXISTS aa_patched_apps_fix;" +
                                finalCommand + "'").getStreamLogsWithLabels());

                try {
                    this.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (suitableMethodFound) {


                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_patched_apps AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    finalCommand +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_patched_apps\";'").getInputStreamLog().length() <= 4) {
                        suitableMethodFound = false;
                    } else {
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "aa_patched_apps");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                changeStatus(patchappstatus, 1, true);
                                showRebootButton();
                                patchapps.setText(getString(R.string.unpatch) + getString(R.string.patch_custom_apps));
                            }
                        });
                    }
                }
                dialog.dismiss();
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "custom_apps");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }




    public void patchrailassistant(int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__rail_assistant_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }



                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_assistant_rail;\n" + finalCommand + "'").getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_assistant_rail AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" +
                                finalCommand +
                                "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_assistant_rail\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_assistant_rail");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(assistanimstatus, 1, true);
                            showRebootButton();
                            assistanim.setText(getString(R.string.disable_tweak_string) + getString(R.string.enable_assistant_animation_in_navbar));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_assistant_rail");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void patchforspeed(int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_gps_sensor\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"CarSensorParameters__max_parked_speed_wheel_sensor\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__chained\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"VisualPreview__unchained_experiment_id\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, extensionVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"GearSnacks__parked_gears\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,\"999\",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("DELETE FROM Flags WHERE name=\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"CarSensorParameters__max_parked_speed_wheel_sensor\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"ParkingStateSmoothing__enable\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"VisualPreviewVisibilityControl__require_high_accuracy_speed_sensor\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"GearSnacks__parked_gears\";");
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }


                if (suitableMethodFound) {


                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'DROP TRIGGER IF EXISTS aa_speed_hack;\n" + finalCommand + "'").getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_speed_hack AFTER DELETE\n" +
                                    "ON FlagOverrides\n" +
                                    "BEGIN\n" +
                                    finalCommand +
                                    "END;'\n"
                    ).getStreamLogsWithLabels());
                    if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_speed_hack\";'").getInputStreamLog().length() <= 4) {
                        suitableMethodFound = false;
                    } else {
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "aa_speed_hack");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                changeStatus(noSpeedRestrictionsStatus, 1, true);
                                showRebootButton();
                                nospeed.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.unlimited_scrolling_when_driving));
                            }
                        });
                    }
                }
                dialog.dismiss();
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_speed_hack");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void multiDisplay(int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__clustersim_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__gal_munger_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__cluster_launcher_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MultiDisplay__power_saving_configuration_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }


                appendText(logs, "\n\n--  run SQL method   --");

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS multi_display;" + finalCommand + "'"
                ).getStreamLogsWithLabels());




                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER multi_display AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" +
                                finalCommand +
                                "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"multi_display\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "multi_display");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(mdstatus, 1, true);
                            showRebootButton();
                            mdbutton.setText(getString(R.string.disable_tweak_string) + getString(R.string.multi_display_string));
                        }
                    });
                }


                dialog.dismiss();
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "multi_display");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void patchfortouchlimit(int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__drawer_default_allowed_taps_touchpad\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__max_permits\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999.0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__enable_speed_bump_projected\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__keyboard_force_disabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__lockout_ms\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,80,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_per_sec\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999.0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touch\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentForwardBrowse__invisalign_default_allowed_items_touchpad\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__speedbump_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Mesquite__speedbump_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__speedbump_force_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"McFly__speedbump_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Media__projected_speedbump_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__speedbump_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__speedbump_map_interactivity_events_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__speedbump_non_scroll_events_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__speedbump_max_grid_list_size\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__transcription_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__speedbump_max_list_size\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__max_list_size\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Watevra__max_pane_list_size\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__keyboard_force_disabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__sixtap_force_enabled\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ContentBrowse__permits_chart\",(SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__drawer_default_allowed_taps_touchpad\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__max_permits\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__enable_speed_bump_projected\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__keyboard_force_disabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__lockout_ms\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__permits_per_sec\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__speedbump_unrestricted_consecutive_scroll_up_actions\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentForwardBrowse__invisalign_default_allowed_items_rotary\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentForwardBrowse__invisalign_default_allowed_items_touch\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentForwardBrowse__invisalign_default_allowed_items_touchpad\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Dialer__speedbump_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Mesquite__speedbump_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__speedbump_force_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"McFly__speedbump_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Media__projected_speedbump_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Watevra__speedbump_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Watevra__speedbump_map_interactivity_events_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Watevra__speedbump_non_scroll_events_enabled\";");
            finalCommand.append("DELETE FROM Flags WHERE name=\"Watevra__speedbump_max_list_size\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Watevra__transcription_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"Watevra__speedbump_max_grid_list_size\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__keyboard_force_disabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__sixtap_force_enabled\";");

            finalCommand.append("DELETE FROM Flags WHERE name=\"ContentBrowse__permits_chart\";");

        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());


                appendText(logs, "\n\n-- Run SQL Commands  --");
                {

                    if (xpmode) {
                        appendText(logs, "\n\n--  killing Google Play Services   --");
                        appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                    }


                    appendText(logs, "\n\n--  run SQL method   --");
                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TRIGGER IF EXISTS aa_six_tap;\n" + finalCommand + "'"
                    ).getStreamLogsWithLabels());

                    appendText(logs, runSuWithCmd(
                            path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                    "'CREATE TRIGGER aa_six_tap AFTER DELETE\n" +
                                    "ON FlagOverrides\n BEGIN\n" + finalCommand + "END;'\n"
                    ).getStreamLogsWithLabels());
                    if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_six_tap\";'").getInputStreamLog().length() <= 4) {
                        suitableMethodFound = false;
                    } else {
                        appendText(logs, "\n--  end SQL method   --");
                        save(true, "aa_six_tap");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                changeStatus(taplimitstatus, 1, true);
                                showRebootButton();
                                taplimitat.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.disable_speed_limitations));
                            }
                        });
                    }
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_six_tap");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();
    }

    public void navpatch(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__custom_startup_app_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_new_startup;\nDROP TRIGGER IF EXISTS aa_new_startup_cleanup;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_new_startup AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_new_startup\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_new_startup");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(navstatus, 1, true);
                            showRebootButton();
                            newStartupTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.custom_startup_option));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_new_startup");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }

    public void userSeatTogglePatch(int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"DriverPositionSetting__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_userseat_tweak;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_userseat_tweak AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_userseat_tweak\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_userseat_tweak");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(userSeatTweakStatus, 1, true);
                            showRebootButton();
                            userSeatTweakButton.setText(getString(R.string.disable_tweak_string) + getString(R.string.custom_hotseat_option));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_userseat_tweak");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }



    public void disableBatteryWarning(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__warning_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS battery_saver_warning;\n" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER battery_saver_warning AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"battery_saver_warning\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "battery_saver_warning");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(batteryWarningStatus, 1, true);
                            showRebootButton();
                            batteryWarning.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.battery_warning));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "battery_saver_warning");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }

    public void battOutline(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BatterySaver__icon_outline_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_battery_outline;\nDELETE FROM Flags WHERE name=\"BatterySaver__icon_outline_enabled\";\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_battery_outline AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_battery_outline\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_battery_outline");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(batteryOutlineStatus, 1, true);
                            showRebootButton();
                            batteryoutline.setText(getString(R.string.disable_tweak_string) + getString(R.string.battery_outline_string));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_battery_outline");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();


    }

    public void opaqueStatusBar(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Boardwalk__status_bar_force_opaque\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_sb_opaque;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_sb_opaque AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_sb_opaque\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_sb_opaque");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(opaqueStatus, 1, true);
                            showRebootButton();
                            statusbaropaque.setText(getString(R.string.disable_tweak_string) + getString(R.string.statb_opaque_string));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_sb_opaque");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void activateCoolwalk(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Coolwalk__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Coolwalk__rail_back_button\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Assistant__coolwalk_suggestions_grpc_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Coolwalk__rail_back_button\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("DELETE FROM Flags WHERE name=\"Coolwalk__rail_back_button\";");
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_activate_coolwalk;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_activate_coolwalk AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_activate_coolwalk\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_activate_coolwalk");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(coolwalkTweakStatus, 1, true);
                            showRebootButton();
                            coolwalkTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_tweak));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_activate_coolwalk");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void activateMirroringApp(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"MirrorApp__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_activate_mirrorapp;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_activate_mirrorapp AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_activate_mirrorapp\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_activate_mirrorapp");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(mirrorAppTweakStatus, 1, true);
                            showRebootButton();
                            mirrorAppTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.casting_app));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_activate_mirrorapp");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void activatesmsdecline(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Messaging__decline_call_message_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Messaging__template_ui_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_activate_declinesms;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_activate_declinesms AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_activate_declinesms\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_activate_declinesms");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(declineSmsTweakStatus, 1, true);
                            showRebootButton();
                            declineSmsTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.decline_message_tweak));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_activate_declinesms");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }


    public void forceAssistantTranscript(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Assistant__transcription_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS assistant_activate_transcript;\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER assistant_activate_transcript AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"assistant_activate_transcript\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "assistant_activate_transcript");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(coolwalkTweakStatus, 1, true);
                            showRebootButton();
                            coolwalkTweak.setText(getString(R.string.disable_tweak_string) + getString(R.string.coolwalk_tweak));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "assistant_activate_transcript");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void forceNoBt(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"BluetoothPairing__car_bluetooth_service_disable\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"BluetoothPairing__car_bluetooth_service_skip_pairing\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS bluetooth_pairing_off;\n" +
                                "DELETE FROM Flags WHERE name=\"BluetoothPairing__car_bluetooth_service_disable\";\n" +
                                "DELETE FROM Flags WHERE name=\"BluetoothPairing__car_bluetooth_service_skip_pairing\";\n"
                                + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER bluetooth_pairing_off AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"bluetooth_pairing_off\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "bluetooth_pairing_off");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(btstatus, 1, true);
                            showRebootButton();
                            bluetoothoff.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.bluetooth_auto_connect));
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "bluetooth_pairing_off");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void disableTelemetry(View view, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarEventLoggerRefactorFeature__convert_car_setup_analytics_telemetry\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__is_wifi_kbps_logging_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__log_battery_temperature\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarServiceTelemetry__wifi_latency_log_frequency_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",0,\"ConnectivityLogging__heartbeat_interval_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"TelemetryDriveIdFeature__enable_log_event_validation\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"TelemetryDriveIdFeature__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"UsbStatusLoggingFeature__monitor_usb_ping_telemetry_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_frx_setup_logging_via_gearhead\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.gms.car\",0,\"AudioStatsLoggingFeature__audio_stats_logging_period_milliseconds\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,99999999,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"FrameworkMediaStatsLoggingFeature__is_media_stats_queue_time_logging_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__num_background_threads\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__include_extra_events\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__enable_heartbeat\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"WifiChannelLogging__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__session_info_dump_size\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"BluetoothMetadataLogger__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.gms.car\",0,\"CarEventLoggerRefactorFeature__convert_car_analytics_telemetry\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Bugfix__sensitive_permissions_extra_logging\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__log_bluetooth_rssi\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__save_log_when_usb_starts\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__skip_retroactive_usb_logging\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"InternetConnectivityLogging__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Telemetry__local_logging\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"WirelessProjectionInGearhead__wireless_wifi_additional_start_logging\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"Dialer__r_telemetry_enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"AssistantSilenceDiagnostics__enabled\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_continuous_telemetry_binding\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"TelemetryDriveIdForGearheadFeature__enable_telemetry_impl_conversion\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,0,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__long_session_timeout_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__short_session_timeout_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__session_timeout_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType,  name, user, boolVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"ConnectivityLogging__use_realtime_if_invalid\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1) ,1,1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS kill_telemetry;" +
                                "DELETE FROM Flags WHERE name LIKE \"%telemetry%\" AND packageName=\"com.google.android.projection.gearhead\";\n" +
                                "DELETE FROM Flags WHERE name LIKE \"%telemetry%\" AND packageName=\"com.google.android.gms.car\";" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER kill_telemetry AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"kill_telemetry\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "kill_telemetry");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(telemetryStatus, 1, true);
                            showRebootButton();
                            disableTelemetryButton.setText(getString(R.string.re_enable_tweak_string) + getString(R.string.telemetry_string));
                        }
                    });
                }

                dialog.dismiss();
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "kill_telemetry");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        }.start();

    }

    public void setHunDuration(View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__hun_default_heads_up_timeout_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        runOnUiThread(new Thread() {
            @Override
            public void run() {

                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_hun_ms;\n" +
                                "DELETE FROM Flags WHERE name=\"SystemUi__hun_default_heads_up_timeout_ms\";\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_hun_ms AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_hun_ms\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_hun_ms");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(messagesHunStatus, 1, true);
                            showRebootButton();
                            saveValue(value, "messaging_hun_value");
                            currentlySetHun.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_hun_ms");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    public void setMediaHunDuration(View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__media_hun_in_rail_widget_timeout_ms\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }


        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }


                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_media_hun;\n" +
                                "DELETE FROM Flags WHERE name=\"SystemUi__media_hun_in_rail_widget_timeout_ms\";\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_media_hun AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_media_hun\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_media_hun");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(mediaHunStatus, 1, true);
                            showRebootButton();
                            saveValue(value, "media_hun_value");
                            currentlySetMediaHun.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }

                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }

                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_media_hun");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });
        dialog.dismiss();
    }

    public void setUSBbitrate(final double value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());


        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_1080p_usb\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 16000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_1080p_usb_hevc\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 3000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_480p_usb\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 8000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_480p_usb_hevc\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 1000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_720p_usb\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 12000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_720p_usb_hevc\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 2000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }


                appendText(logs, "\n\n-- Run SQL Commands  --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_bitrate_usb;\n DELETE FROM Flags WHERE name LIKE \"VideoEncoderParamsFeature%\";" +
                                finalCommand + "'"
                ).getStreamLogsWithLabels());



                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_bitrate_usb AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_bitrate_usb\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_bitrate_usb");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(usbBitrateStatus, 1, true);
                            showRebootButton();
                            saveFloat((float) value, "usb_bitrate_value");
                            currentlySetUSBSeekbar.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_bitrate_usb");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    public void setWiFiBitrate(final double value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_1080p_wireless\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 16000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_1080p_wireless_hevc\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 3000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_480p_wireless\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 8000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_480p_wireless_hevc\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 1000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_720p_wireless\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 12000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, floatVal, committed) VALUES (\"com.google.android.gms.car\",0,\"VideoEncoderParamsFeature__bitrate_720p_wireless_hevc\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + String.format("%.0f", 2000000 * value) + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());


                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS aa_bitrate_wireless;\n DELETE FROM Flags WHERE name LIKE \"VideoEncoderParamsFeature%\";\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER aa_bitrate_wireless AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"aa_bitrate_wireless\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "aa_bitrate_wireless");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(wifiBitrateStatus, 1, true);
                            showRebootButton();
                            saveFloat((float) value, "wifi_bitrate_value");
                            currentlySetWiFiSeekbar.setText(getString(R.string.currently_set) + value);
                        }
                    });

                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "aa_bitrate_wireless");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    private void setCalendarEvents(View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);

        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"McFly__num_days_in_agenda_view\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
        }

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());
                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }

                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'DROP TRIGGER IF EXISTS calendar_aa_tweak;\n" + "DELETE FROM Flags WHERE name=\"McFly__num_days_in_agenda_view\";\n" + finalCommand + "'"
                ).getStreamLogsWithLabels());


                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER calendar_aa_tweak AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());


                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"calendar_aa_tweak\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    save(true, "calendar_aa_tweak");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            changeStatus(calendarTweakStatus, 1, true);
                            showRebootButton();
                            saveValue(value, "agenda_value");
                            currentlySetAgendaDays.setText(getString(R.string.currently_set) + value);
                        }
                    });
                }
                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", "calendar_aa_tweak");
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }

    public void forceWideScreen(View view, final int value, int usercount) {
        final TextView logs = findViewById(R.id.logs);
        logs.setHorizontallyScrolling(true);
        logs.setMovementMethod(new ScrollingMovementMethod());
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.tweak_loading), true);
        final StringBuilder finalCommand = new StringBuilder();

        for (int i = 0; i <= (usercount - 1); i++) {
            if (multiAccountsMode && !xpmode && !accountsPrefs.getBoolean(String.valueOf(i), false)) {
                continue;
            }
            finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__widescreen_breakpoint_dp\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
            finalCommand.append(i);
            finalCommand.append(",1)," + value + ",1);");
            finalCommand.append(System.getProperty("line.separator"));
            if (value == 3000) {
                finalCommand.append("INSERT OR REPLACE INTO FlagOverrides (packageName, flagType, name, user, intVal, committed) VALUES (\"com.google.android.projection.gearhead\",0,\"SystemUi__regular_layout_max_width_dp\", (SELECT DISTINCT user FROM ApplicationTags WHERE user != \"\" ORDER BY user ASC LIMIT ");
                finalCommand.append(i);
                finalCommand.append(",1)," + value + ",1);");
                finalCommand.append(System.getProperty("line.separator"));
            }
        }

        runOnUiThread(new Thread() {
            @Override
            public void run() {
                String path = getApplicationInfo().dataDir;
                suitableMethodFound = true;
                appendText(logs, "\n\n--  Force stopping Google Play Services   --");
                appendText(logs, runSuWithCmd("am kill all com.google.android.gms").getStreamLogsWithLabels());
                String currentOwner = runSuWithCmd("stat -c \"%U\" /data/data/com.google.android.gms/databases/phenotype.db").getInputStreamLog();
                appendText(logs, "\n\n--  Gaining ownership of the database   --");
                appendText(logs, runSuWithCmd("chown root /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                String currentPolicy = runSuWithCmd("getenforce").getInputStreamLog();
                appendText(logs, "\n\n--  Setting SELINUX to permessive   --");
                appendText(logs, runSuWithCmd("setenforce 0").getStreamLogsWithLabels());
                String decideWhat = new String();

                if (xpmode) {
                    appendText(logs, "\n\n--  killing Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm disable com.google.android.gms").getStreamLogsWithLabels());
                }


                appendText(logs, "\n\n--  run SQL method   --");
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TRIGGER IF EXISTS force_ws;\n DROP TRIGGER IF EXISTS force_no_ws;\n" + finalCommand + "'").getStreamLogsWithLabels());

                switch (value) {
                    case 470: {
                        decideWhat = "force_ws";
                        break;
                    }
                    case 3000: {
                        decideWhat = "force_no_ws";
                        break;
                    }
                }
                appendText(logs, runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'CREATE TRIGGER " + decideWhat + " AFTER DELETE\n" +
                                "ON FlagOverrides\n" +
                                "BEGIN\n" + finalCommand + "END;'\n"
                ).getStreamLogsWithLabels());
                if (runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND name=\"" + decideWhat + "\";'").getInputStreamLog().length() <= 4) {
                    suitableMethodFound = false;
                } else {
                    appendText(logs, "\n--  end SQL method   --");
                    switch (value) {
                        case 470: {
                            changeStatus(forceWideScreenStatus, 1, true);
                            showRebootButton();
                            break;
                        }
                        case 3000: {
                            changeStatus(forceNoWideScreenStatus, 1, true);
                            showRebootButton();
                            break;
                        }
                    }
                    save(true, decideWhat);
                }

                if (xpmode) {
                    appendText(logs, "\n\n--  restoring Google Play Services   --");
                    appendText(logs, runSuWithCmd("pm enable com.google.android.gms").getStreamLogsWithLabels());
                }

appendText(logs, "\n\n--  Restoring ownership of the database   --");
                appendText(logs, runSuWithCmd("chown " + currentOwner + " /data/data/com.google.android.gms/databases/phenotype.db").getStreamLogsWithLabels());

                if (currentPolicy.toLowerCase().equals("permissive")) {
                    appendText(logs, "\n\n--  Restoring SELINUX   --");
                    appendText(logs, runSuWithCmd("setenforce 1").getStreamLogsWithLabels());
                }
                dialog.dismiss();
                if (!suitableMethodFound) {
                    final DialogFragment notSuccessfulDialog = new NotSuccessfulDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("tweak", decideWhat);
                    bundle.putString("log", logs.getText().toString());
                    notSuccessfulDialog.setArguments(bundle);
                    notSuccessfulDialog.show(getSupportFragmentManager(), "NotSuccessfulDialog");
                }
            }
        });

    }



    public static StreamLogs runSuWithCmd(String cmd) {
        DataOutputStream outputStream = null;
        InputStream inputStream = null;
        InputStream errorStream = null;

        StreamLogs streamLogs = new StreamLogs();
        streamLogs.setOutputStreamLog(cmd);

        try {
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            inputStream = su.getInputStream();
            errorStream = su.getErrorStream();
            outputStream.writeBytes(cmd + "\n");
            outputStream.flush();

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            streamLogs.setInputStreamLog(readFully(inputStream));
            streamLogs.setErrorStreamLog(readFully(errorStream));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return streamLogs;
    }

    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }


    private void appendText(final TextView textView, final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.append(s);
            }
        });
    }

    public void loadStatus(final String path) {

        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "",
                getString(R.string.loading), true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String get_names = runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"FlagOverrides\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"after_delete\";" +
                                "SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"Flags\" AND name=\"aa_patched_apps\";'").getInputStreamLog();
                String[] lines = get_names.split(System.getProperty("line.separator"));
                for (int i = 0; i < lines.length; i++) {
                    save(true, lines[i]);
                }
                dialog.dismiss();
            }
        });

    }

    public void getAndRemoveOptionsSelected() {
        final TextView log = findViewById(R.id.logs);
        final String[] allTriggerString = {new String()};
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading), true);
        new Thread() {
            @Override
            public void run() {

                String path = appDirectory;
                allTriggerString[0] = path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'";
                String get_names = runSuWithCmd(
                        path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " +
                                "'SELECT name FROM sqlite_master WHERE type=\"trigger\" AND tbl_name=\"FlagOverrides\";'").getInputStreamLog();
                appendText(log, get_names);
                String[] lines = get_names.split(System.getProperty("line.separator"));
                final StringBuilder finalCommand = new StringBuilder();
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DROP TABLE FlagOverrides;'").getOutputStreamLog());
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DELETE FROM Flags WHERE name=\"com.google.android.projection.gearhead\";'").getOutputStreamLog());
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'DELETE FROM Flags WHERE name=\"com.google.android.gms.car\";'").getOutputStreamLog());

                for (int i = 0; i < lines.length; i++) {
                    finalCommand.append("DROP TRIGGER IF EXISTS " + lines[i] + ";");
                    finalCommand.append("\n");
                }

                for (int i = 0; i < lines.length; i++) {
                    appendText(log, runSuWithCmd(path + "/sqlite3 -batch /data/data/com.google.android.gms/databases/phenotype.db " + "'" + finalCommand + "'").getOutputStreamLog());
                }
                appendText(log, runSuWithCmd(path + "/sqlite3 /data/data/com.google.android.gms/databases/phenotype.db " + "'CREATE TABLE FlagOverrides (packageName TEXT NOT NULL, user TEXT NOT NULL, name TEXT NOT NULL, flagType INTEGER NOT NULL, intVal INTEGER, boolVal INTEGER, floatVal REAL, stringVal TEXT, extensionVal BLOB, committed, PRIMARY KEY(packageName, user, name, committed));'").getOutputStreamLog());
                dialog.dismiss();
            }

        }.start();

        return;
    }

    public void showRebootButton() {
        runOnUiThread(new Thread() {
            @Override
            public void run() {
                final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.reboot_button_anim);

                if (!animationRun) {
                    rebootButton.setVisibility(View.VISIBLE);
                    rebootButton.startAnimation(anim);
                    animationRun = true;
                }
            }
        });

    }

    public static void openApp(Context context, String packageName) {
        if (isAppInstalled(context, packageName))
            if (isAppEnabled(context, packageName)) {
                PackageManager pm = context.getPackageManager();
                Intent launchIntent = new Intent("com.google.android.projection.gearhead.SETTINGS");
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
            } else
                Toast.makeText(context, context.getString(R.string.not_enabled_warning), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, context.getString(R.string.not_installed_warning), Toast.LENGTH_SHORT).show();
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return false;
    }

    private static boolean isAppEnabled(Context context, String packageName) {
        Boolean appStatus = false;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (ai != null) {
                appStatus = ai.enabled;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appStatus;
    }

    private void changeStatus(ImageView resource, int status, boolean doAnimation) {
        final RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(400);
        rotate.setInterpolator(new LinearInterpolator());
        switch (status) {
            case 2: {
                resource.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
                resource.setColorFilter(Color.argb(255, 0, 255, 0));
                break;
            }
            case 0: {
                resource.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                resource.setColorFilter(Color.argb(255, 255, 0, 0));
                break;
            }
            case 1: {
                resource.setImageDrawable(getDrawable(R.drawable.ic_baseline_remove_circle_24));
                resource.setColorFilter(Color.argb(255, 255, 255, 0));
                break;
            }
        }
        if (doAnimation) {
            resource.startAnimation(rotate);
        }
    }


}
