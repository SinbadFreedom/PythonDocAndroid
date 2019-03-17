package com.panda_doc.python.note;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.python.R;
import com.panda_doc.python.conf.Conf;
import com.panda_doc.python.tasks.TasksActivity;
import com.panda_doc.python.tasks.TasksFragment;
import com.panda_doc.python.util.NumberUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DocNoteFragment extends Fragment {

    NoteAdapter noteAdapter = new NoteAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_doc_note, container, false);

        /** 笔记列表*/
        ListView listView = (ListView) root.findViewById(R.id.note_list);
        listView.setAdapter(noteAdapter);
        this.getNoteByDocId(TasksFragment.currentPageNum);

        /** 关闭手势滑动*/
        ((TasksActivity) getActivity()).closeSlide();
        return root;
    }

    /**
     * 获取文章对应的笔记
     */
    private void getNoteByDocId(String id) {
        if (null == id) {
            Log.e(Conf.LOG_TAG, "getNoteByDocId null == id");
            return;
        }

        boolean isInt = NumberUtil.isInteger(id);
        if (!isInt) {
            /** 当前文章编号不是整型,比如index,返回*/
            Log.e(Conf.LOG_TAG, " getNoteByDocId isInt false");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this.getContext());
        String url = Conf.URL_LOG_TAG + "?page=" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonObject = new JSONArray(response);
                            ArrayList<Note> notes = new ArrayList<>();
                            for (int i = 0; i < jsonObject.length(); i++) {
                                JSONObject noteObj = (JSONObject) jsonObject.get(i);
                                String userIconUrl = noteObj.getString(Conf.KEY_USER_ICON);
                                String userName = noteObj.getString(Conf.KEY_USER_NAME);
                                String noteText = noteObj.getString(Conf.KEY_NOTE_INFO);

                                Note note = new Note(userIconUrl, userName, noteText);
                                notes.add(note);
                            }
                            noteAdapter.setAllTitles(notes);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(Conf.LOG_TAG, " response " + response);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
}