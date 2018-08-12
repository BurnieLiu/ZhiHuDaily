package com.burnie.zhihu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by liuli on 2018/3/6.
 */

public class RxJava2DemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RxJava2DemoActivity.class.getSimpleName();

    private ObservableEmitter<Integer> mEmitter;
    private Button mBtEmitter;
    /**
     * 装载Disposable clear 时 dispose 所有
     */
    private CompositeDisposable mCompositeDisposable;
    private Button mBtCommon;
    private Button mBtMap;
    private Button mBtFlatmap;
    private Button mBtZip;
    private Button mBtSample;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBtCommon = findViewById(R.id.common);
        mBtEmitter = findViewById(R.id.bt_emitter);
        mBtMap = findViewById(R.id.map);
        mBtFlatmap = findViewById(R.id.flatmap);
        mBtZip = findViewById(R.id.zip);
        mBtSample = findViewById(R.id.sample);

        mBtMap.setOnClickListener(this);
        mBtFlatmap.setOnClickListener(this);
        mBtCommon.setOnClickListener(this);
        mBtEmitter.setOnClickListener(this);
        mBtZip.setOnClickListener(this);
        mBtSample.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.common) {
            doRxJavaAction0();
        } else if (v.getId() == R.id.bt_emitter) {
            doEmit0();
        } else if (v.getId() == R.id.map) {
            doRxJavaAction1();
        } else if (v.getId() == R.id.flatmap) {
            doRxJavaAction2();
        } else if (v.getId() == R.id.zip) {
            doRxJavaAction3();
        } else if (v.getId() == R.id.sample) {
            doRxJavaAction4();
        }
    }

    /**
     * sample采样
     */
    private void doRxJavaAction4() {
        Observable.create(new ObservableOnSubscribe<Long>() {
            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {
                for (long i = 0; ; i++) {
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .sample(2, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long num) {
                        Log.i(TAG, String.valueOf(num));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * zip 任意一根水管发送 complete 事件 即可终止emit 动作
     */
    private void doRxJavaAction3() {
        Observable.zip(Observable.fromArray("A", "B", "C", "D", "E").subscribeOn(Schedulers.io()), Observable.fromArray(1, 2, 3, 4, 5, 6).subscribeOn(Schedulers.io()), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.i(TAG, s);
            }
        });
    }

    /**
     * flatMap/concatMap
     * flat 不保证铺平后 顺序 ，concat保证铺平后顺序
     */
    private void doRxJavaAction2() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final Integer integer) throws Exception {
                final ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < 100; i++) {
                    arrayList.add("第" + integer + "组,---" + i);
                }
                return Observable.fromIterable(arrayList).delay(10, TimeUnit.MILLISECONDS);
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * map
     */
    private void doRxJavaAction1() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i <= 99; i++) {
                    Thread.sleep(300);
                    emitter.onNext(i);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer integer) throws Exception {
                return String.valueOf("RaJava is wonderful" + integer);
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void doEmit0() {
        if (mEmitter != null) {
            mEmitter.onNext(new Random().nextInt());
        }
    }

    /**
     * 最基本的使用 Action0&filter过滤器
     */
    private void doRxJavaAction0() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                mEmitter = emitter;
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(4);
//                int a = 1 / 0;
//                emitter.onComplete();

//                emitter.onError(new Throwable("heihei"));

                emitter.onNext(5);
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer != -1 && integer != 0;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        mCompositeDisposable = new CompositeDisposable();
                        mCompositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, String.valueOf(integer));

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "complete");

                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }
}
