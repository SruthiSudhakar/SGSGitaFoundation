package com.example.meena.sgsgitafoundation;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.w3c.dom.Text;

public class VisionActivity extends AppCompatActivity {
    TextView title;
    TextView paraone;
    TextView paratwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);
        title = (TextView) findViewById(R.id.title);
        paraone = (TextView) findViewById(R.id.paraone);
        paratwo = (TextView) findViewById(R.id.paratwo);
        paraone.setMovementMethod(new ScrollingMovementMethod());

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.visionToolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        String s= getIntent().getStringExtra("Tab");
        switch (s)
        {
            case "Vision":
                title.setTextSize(36);
                title.setText("Vision");
                paraone.setText("\"If the Upanishads present the aim and purpose of human life, the Bhagavad Gita indicates the way to attain them and live a life of fulfillment. By faithfully following that path shown by the Bhagavad Gita, man can make his life sublime, leave behind his footprints on the sands of time.\"");
                paratwo.setText("--Parama Pujya Sri Sri Sri Ganapathy Sachchidananda Swamiji");
                break;
            case "Program":
                title.setTextSize(20);
                title.setText("Bhagavad Gita Mahayagna program");
                paraone.setHeight(700);
                paraone.setText("Gita Mahayagna program has been initiated per vision and blessings of Parama Pujya Sri Ganapathy Sachchidananda Swamiji. As part of this program, 220 students of ages 5 – 50 years underwent an intensive training and successfully completed memorization of entire Bhagavad Gita in just 10 months since its inception in 2015.\n" +
                        "\n" +
                        "To encourage and inspire everyone to memorize entire Bhagavad Gita, Sri Swamiji has made a resolve to present Gold medals and certificates to anyone who memorizes all 700 verses and performs in Mahayagna event in presence of Parama Pujya Sri Swamiji.\n" +
                        "\n" +
                        "In this Mahayagna program, participants memorize all 18 chapters of the Bhagavad Gita within 10 months under able guidance of well-trained teachers. Participants in this program will be taught the precise pronunciation of each verse. This website provides unique tutorial application, for learning all 18 chapters of the Bhagavad Gita.\n" +
                        "\n" +
                        "With its unique model of classes in person, and virtual teaching online, Mahayagna program is growing rapidly with centers across various countries.");
                paratwo.setText("");
                break;
            case "Journey":
                title.setText("Journey");
                paraone.setText("Pujya Sri Swamiji expressed His wish to see at least 18 children memorize all 18 Chapters of Srimad Bhagavad Gita during Karya Siddhi Hanuman temple inauguration in Dallas, TX in 2015. Astoundingly, inspired by the call and out of love for Pujya Sri Swamiji, Whom they lovingly call \"Tataji\", 43 children, as young as 7 years to 16 years memorized all 700 shlokas in less that 10 months. They also learnt the meanings of selected shlokas and got ready by the next year visit of Pujya Sri Swamiji to the US in summer 2016.\n" +
                        "\n" +
                        "That’s how this beautiful Mahayagna journey started with children who mesmerized the whole world. Millions watched the program that was live webcast and also telecast on various TV Channels in India and in the US.");
                paratwo.setText("...its an incredible journey!");
                break;
        }
    }
}
