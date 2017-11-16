package cmps121.qwikax.Node_Related;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmps121.qwikax.R;


public class ListViewFragment extends android.support.v4.app.Fragment {

    private ListView _listView;
 //   private boolean _runMode;

    public ListViewFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
        String[] appArray = new String[allApps.size()];
        for(int i=0; i<allApps.size(); i++){
            String app = removeChars(allApps.get(i).toString());
            appArray[i] = app;
        }


       // String[] items = {"thing 1", "thing 2", "things3"};
        ListView listView = (ListView) view.findViewById(R.id.testlist);
        for(int i=0; i<allApps.size(); i++){
            String app = removeChars(allApps.get(i).toString());
            appArray[i] = app;
        }
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                appArray
        );
        if(listView != null) {
            listView.setAdapter(listViewAdapter);
        }
        return view;

/*
        _listView = (ListView) getActivity().findViewById(R.id.testlist);
        if(_listView != null) {
            setList(_listView);
        }
        return _listView;
*/
/*
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
        String[] appArray = new String[allApps.size()];
        for(int i=0; i<allApps.size(); i++){
            String app = removeChars(allApps.get(i).toString());
            appArray[i] = app;
        }

        ArrayList<String> appList = new ArrayList<String>();
        appList.addAll(Arrays.asList(appArray));
        ArrayAdapter<String> appAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, appList);


        if(_listView != null){
            _listView.setAdapter(appAdapter);
        }
        return _listView;*/
    }

    public void setList(ListView apps){

        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
        String[] appArray = new String[allApps.size()];
        for(int i=0; i<allApps.size(); i++){
            String app = removeChars(allApps.get(i).toString());
            appArray[i] = app;
        }

        ArrayList<String> appList = new ArrayList<String>();
        appList.addAll(Arrays.asList(appArray));
        ArrayAdapter<String> appAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, appList);

        apps.setAdapter(appAdapter);
    }

    public String removeChars(String s){
        String chopped = "Could Not Find";
        Pattern pattern = Pattern.compile("(\\S+om\\S+)");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()){
            chopped = matcher.group(1).substring(0,matcher.group(1).length() - 1);
        }
        return chopped;
    }
    /*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    /*
    public static ListViewFragment newInstance(String param1, String param2) {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_view, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

*/
}