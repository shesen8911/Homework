package com.example.homework;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView detailTv = view. findViewById(R.id.tvDetail);
        DetailFragmentArgs args = DetailFragmentArgs.fromBundle(getArguments());

        //判斷訊號強弱
        String signal;
        if (args.getRssi() >= -50)
            signal = " 訊號強";
        else if (args.getRssi() >= -70)
            signal = " 訊號中";
        else
            signal = " 訊號弱";
        detailTv.setText(
                "MAC位址:\n" + args.getMac() + "\n\n" +
                "訊號強度:\n" + args.getRssi()+ signal + "\n\n" +
                "訊號內容:\n" + args.getContent());
    }
    /*網路上茶的藍芽強度:
      -50 ~   0 dBm  訊號強
      -70 ~ -50 dBm  訊號中
           <-70 dBm  訊號弱*/
}