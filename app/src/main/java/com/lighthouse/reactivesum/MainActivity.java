package com.lighthouse.reactivesum;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.a_value)
    EditText aEditText;

    @BindView(R.id.b_value)
    EditText bEditText;

    @BindView(R.id.result_value)
    TextView resultValue;

    private Observable<Double> aObservable;
    private Observable<Double> bObservable;
    private Observable<Double> resultObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        createObservables();

        resultObservable.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(nextUpdate -> resultValue.setText(nextUpdate.toString()));
    }

    private void createObservables() {
        aObservable = createEditTextObservable_A();
        bObservable = createEditTextObservable_B();
        resultObservable = createCombineLatestResultObservable();
    }

    @RxLogObservable
    private Observable<Double> createEditTextObservable_A() {
        return RxTextView.textChanges(aEditText)
                         .observeOn(AndroidSchedulers.mainThread())
                         .skip(1)
                         .map(textChange -> Double.parseDouble(textChange.toString()));
    }

    @RxLogObservable
    private Observable<Double> createEditTextObservable_B() {
        return RxTextView.textChanges(bEditText)
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribeOn(AndroidSchedulers.mainThread())
                         .skip(1)
                         .map(textChange -> Double.parseDouble(textChange.toString()));
//                         .doOnError(throwable -> resetEditTextB())
//                         .retry();

    }

    @RxLogObservable()
    private Observable<Double> createCombineLatestResultObservable() {
        return Observable.combineLatest(aObservable, bObservable,
                                        (a, b) -> a + b)
                         .observeOn(AndroidSchedulers.mainThread());
    }
}
