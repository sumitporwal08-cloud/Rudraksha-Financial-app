package com.rudraksha.financial;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import org.json.*;

public class MainActivity extends Activity {
    int brown = Color.rgb(49,31,20), gold = Color.rgb(184,127,54), cream = Color.rgb(250,247,241);
    LinearLayout root, body; EditText name, mobile, city, amount, notes, ref, trackMobile;
    SharedPreferences prefs;
    public void onCreate(Bundle b){ super.onCreate(b); prefs=getSharedPreferences("rudraksha",0); showHome(); }
    TextView text(String s,int size,int color){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); t.setPadding(0,8,0,8); return t; }
    LinearLayout column(){ LinearLayout l=new LinearLayout(this); l.setOrientation(1); l.setPadding(22,16,22,22); return l; }
    void base(String title){
        root=column(); root.setBackgroundColor(cream); setContentView(root);
        TextView brand=text("RUDRAKSHA FINANCIAL SERVICES",20,Color.WHITE); brand.setTypeface(null,Typeface.BOLD); brand.setPadding(20,22,16,22); brand.setBackgroundColor(brown); root.addView(brand);
        TextView sub=text(title,22,brown); sub.setTypeface(null,Typeface.BOLD); sub.setPadding(0,18,0,10); root.addView(sub);
        body=column(); body.setPadding(0,0,0,0); root.addView(body,new LinearLayout.LayoutParams(-1,-1));
    }
    Button btn(String label,Runnable r){ Button b=new Button(this); b.setText(label); b.setTextColor(Color.WHITE); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(brown)); b.setOnClickListener(v->r.run()); return b; }
    EditText field(String hint,int type){ EditText e=new EditText(this); e.setSingleLine(true); e.setHint(hint); e.setInputType(type); body.addView(e); return e; }
    void showHome(){ base("Welcome"); body.addView(text("Your loan journey starts here.",17,Color.DKGRAY)); body.addView(text("Our Services",18,brown)); body.addView(text("• Home Loan\n• Mortgage Loan\n• Project Loan",16,Color.DKGRAY)); body.addView(btn("Apply for a Loan",()->showApply())); body.addView(btn("Track Application",()->showTrack())); body.addView(btn("Employee / Admin",()->showTeamLogin())); body.addView(text("Indore, Madhya Pradesh\nPU4, Behind C21 Mall, Vijay Nagar",13,Color.GRAY)); }
    void showApply(){ base("Loan Enquiry"); name=field("Full name",1); mobile=field("Mobile number",3); city=field("City",1); Spinner sp=new Spinner(this); String[] opts={"Home Loan","Mortgage Loan","Project Loan"}; sp.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,opts)); body.addView(text("Loan type",14,brown)); body.addView(sp); amount=field("Approx. loan amount (₹)",2); notes=field("Brief requirement",1); body.addView(btn("Submit Enquiry",()->{ String n=name.getText().toString().trim(), m=mobile.getText().toString().trim(); if(n.isEmpty()||m.length()<10){ Toast.makeText(this,"Enter name and valid mobile number",0).show(); return; } String id="RFS"+(System.currentTimeMillis()%1000000); try { JSONArray a=new JSONArray(prefs.getString("leads","[]")); JSONObject o=new JSONObject(); o.put("id",id);o.put("name",n);o.put("mobile",m);o.put("city",city.getText().toString());o.put("type",sp.getSelectedItem().toString());o.put("amount",amount.getText().toString());o.put("notes",notes.getText().toString());o.put("status","Received");a.put(o);prefs.edit().putString("leads",a.toString()).apply(); new AlertDialog.Builder(this).setTitle("Enquiry saved").setMessage("Your reference number: "+id+"\nPlease save it to track your enquiry.").setPositiveButton("OK",(d,w)->showHome()).show(); }catch(Exception ex){ Toast.makeText(this,"Could not save enquiry",0).show(); } })); body.addView(btn("Back",()->showHome())); }
    void showTrack(){ base("Track Application"); ref=field("Reference number",1); trackMobile=field("Registered mobile number",3); body.addView(btn("Find Application",()->{ try{JSONArray a=new JSONArray(prefs.getString("leads","[]")); for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i);if(o.getString("id").equalsIgnoreCase(ref.getText().toString().trim())&&o.getString("mobile").equals(trackMobile.getText().toString().trim())){new AlertDialog.Builder(this).setTitle("Application Status").setMessage("Applicant: "+o.optString("name")+"\nLoan: "+o.optString("type")+"\nStatus: "+o.optString("status")).setPositiveButton("OK",null).show();return;}} Toast.makeText(this,"No matching application found on this device",0).show();}catch(Exception e){} })); body.addView(btn("Back",()->showHome())); }
    void showTeamLogin(){ base("Team Login"); EditText pin=field("Team PIN",129); body.addView(btn("Login",()->{if(pin.getText().toString().equals("2580"))showTeam();else Toast.makeText(this,"Incorrect PIN",0).show();})); body.addView(text("Demo PIN: 2580 — change before real use",12,Color.RED)); body.addView(btn("Back",()->showHome())); }
    void showTeam(){ base("Team Dashboard"); try{JSONArray a=new JSONArray(prefs.getString("leads","[]")); body.addView(text("Saved enquiries: "+a.length(),16,brown)); for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i); LinearLayout card=column(); card.setBackgroundColor(Color.WHITE); TextView t=text(o.optString("id")+" • "+o.optString("name")+"\n"+o.optString("type")+" • "+o.optString("mobile")+"\nStatus: "+o.optString("status"),15,Color.DKGRAY);card.addView(t); Button update=btn("Update Status",()->{String[] statuses={"Received","Contacted","Documents Pending","In Process","Approved","Rejected"};new AlertDialog.Builder(this).setTitle("Update status").setItems(statuses,(d,w)->{o.put("status",statuses[w]);try{prefs.edit().putString("leads",a.toString()).apply();showTeam();}catch(Exception e){}}).show();});card.addView(update);body.addView(card);} }catch(Exception e){} body.addView(btn("Back",()->showHome())); }
}
