import java.io.IOException;
import java.io.PrintWriter;
//import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.Cookie;

import com.bkw.GenericBean;
import com.bkw.GenericDataAccess;
import com.bkw.IGenericField;
import com.bkw.translatedstring.TranslatedString;

public class TranslatedStringServlet extends HttpServlet {

    protected String escapeSQL(String value) {
        value.replace("%","\\%");
        value.replace("_","\\_");
        return value;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        Map<String,String[]> parameterMap=request.getParameterMap();
        String stringCode=parameterMap.get("stringCode")[0];
        String languageCode=parameterMap.get("languageCode")[0];
        String countryCode=parameterMap.get("countryCode")[0];

        TranslatedString ts=new TranslatedString(stringCode, languageCode, countryCode);

        GenericDataAccess.delete(ts,TranslatedString.Field.values());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // TBD: How to convert json to java bean
        Map<String,String[]> parameterMap=request.getParameterMap();
        // Default action to "U" (update)
        String action="U";
        String[] array=parameterMap.get("action");
        if(array!=null && array.length>0) action=array[0];

        // Only "U" (update) and "C" (create) are valid post actions, default to update
        if(!action.equals("U")&&!action.equals("C")) action="U";

        // Extract json from posted data and convert to a new TranslatedString instance
        Reader body=request.getReader();
        StringBuilder sb=new StringBuilder();
        char c[]=new char[100];
        try {
            int chars=body.read(c);
            while(chars>0) {
                sb.append(c,0,chars);
                chars=body.read(c);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        String json=sb.toString();
System.out.println("##JSON1:"+json);
        json=java.net.URLDecoder.decode(json, StandardCharsets.UTF_8.toString());
System.out.println("##JSON2:"+json);
        TranslatedString ts=new TranslatedString();
        ts.fromJSONString(json);
System.out.println("##TS:"+ts);

        // Handle create
        if(action.equals("C"))
            GenericDataAccess.create(ts,TranslatedString.Field.values());
        else
            GenericDataAccess.update(ts,TranslatedString.Field.values());

        json="{ \"TranslatedStrings\": []}";
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.println(json);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        /***
        Enumeration<String> headerNames=request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
        String headerName=headerNames.nextElement();
        String headerValue=request.getHeader(headerName);
        System.out.println("##HEADER:"+headerName+"="+headerValue);
        }
        System.out.println("##QUERYSTRING:"+request.getParameterNames());
        Map<String,String[]> parameterMap=request.getParameterMap();
        System.out.println("##PARAMETERMAP:"+parameterMap);
        for(String key:parameterMap.keySet()) {
            String[] vals=parameterMap.get(key);
            for(int i=0; i<vals.length; ++i) {
            System.out.println(key+"["+i+"]="+vals[i]);
            }
            }
        ***/
        String filter="";
        Map<String,String[]> parameterMap=request.getParameterMap();
        String action="R";
        String[] vals=parameterMap.get("action");
        if(vals!=null && vals.length>0) {
            action=vals[0];
            if(!action.equalsIgnoreCase("R")&&!action.equalsIgnoreCase("D")) action="R";
        }

        if(action.equalsIgnoreCase("D")) {
            vals=parameterMap.get("stringCode");
            String stringCode=null;
            if(vals!=null && vals.length>0) {
                stringCode=vals[0];
            }

            vals=parameterMap.get("languageCode");
            String languageCode=null;
            if(vals!=null && vals.length>0) {
                languageCode=vals[0];
            }

            vals=parameterMap.get("countryCode");
            String countryCode=null;
            if(vals!=null && vals.length>0) {
                countryCode=vals[0];
            }
            TranslatedString ts=new TranslatedString(stringCode,languageCode,countryCode);
            GenericDataAccess.delete(ts,TranslatedString.Field.values());

            String json="{ \"TranslatedStrings\": []}";
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(json);
            return;
        }

        vals=parameterMap.get("stringCode");
        String stringCode=null;
        if(vals!=null && vals.length>0) {
            stringCode=vals[0];
            filter=TranslatedString.Field.STRINGCODE.getFieldName()+" LIKE '%"+this.escapeSQL(stringCode)+"%'";
        }

        vals=parameterMap.get("text");
        String text=null;
        if(vals!=null && vals.length>0) {
            text=vals[0];
            filter+=(filter.length()>0?" AND ":"")+TranslatedString.Field.TEXT.getFieldName()+" LIKE '%"+this.escapeSQL(text)+"%'";
        }

        vals=parameterMap.get("ticket");
        String ticket=null;
        if(vals!=null && vals.length>0) {
            ticket=vals[0];
            filter+=(filter.length()>0?" AND ":"")+TranslatedString.Field.TICKET.getFieldName()+" LIKE '%"+this.escapeSQL(ticket)+"%'";
        }

        System.out.println("##FILTER:"+filter);

        /***
        Enumeration<String> parameterNames=request.getParameterNames();
        while(parameterNames.hasMoreElements()) {
        String parameterName=parameterNames.nextElement();
        String[] parameterValues=request.getParameterValues(parameterName);
        if(parameterValues!=null) {
        System.out.print("##QUERYPARMETER:"+parameterName);
        for(int i=0; i<parameterValues.length; ++i) {
            System.out.print((i!=0?",":"")+parameterValues[i]);
        }
        System.out.println();
        }
        }
        Cookie[] cookies=request.getCookies();
        if(cookies!=null) {
        for(int i=0; i<cookies.length; ++i) {
            System.out.println("##COOKIE:"+cookies[i].getName()+"="+cookies[i].getValue());
        }
        }
        ***/

        List<GenericBean<IGenericField, Object>> ts = getTranslatedStrings(filter);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String json="{ \"TranslatedStrings\": [";

        for(int i=0; i<ts.size(); ++i) {
            // Need to convert text to web unicode ("&#"+unicode value+";") to accommodate non-Western European text, such as Chinese
            TranslatedString ts2=(TranslatedString)ts.get(i);
            String txt=ts2.getText();
            String webUnicodeStr="";
            char[] chars=txt.toCharArray();
            for(int j=0; j<chars.length; ++j) {
                webUnicodeStr+="&#"+(int)chars[j]+";";
            }
            ts2.setText(webUnicodeStr);

            String jsonEmp=ts.get(i).toString();
            json+=(i>0?",":"")+jsonEmp;
        }
        json+="]}";
        out.println(json);
    }

    private List<GenericBean<IGenericField, Object>> getTranslatedStrings(String filter) {
        TranslatedString ts=new TranslatedString();
        if(filter.length()>0) {
            //filter+=" AND languageCode='en' AND countryCode='US'";
//System.out.println("##FILTER:"+filter);
            // Get the filtered English translated strings from the database.
            return GenericDataAccess.readFiltered(ts,TranslatedString.Field.values(),filter);
        } else {
            // Get all the translated strings from the database.
            return GenericDataAccess.readAll(ts,TranslatedString.Field.values());
        }
    }

    public static void main(String[] args) {
        TranslatedString ts=new TranslatedString();
        List<GenericBean<IGenericField, Object>> ts2 = GenericDataAccess.readAll(ts,TranslatedString.Field.values());

        String json="{ \"TranslatedStrings\": [";

        for(int i=0; i<ts2.size(); ++i) {
            String jsonEmp=ts2.get(i).toString();
            json+=(i>0?",":"")+jsonEmp;
        }
        json+="]}";
        System.out.println(json);

    }

}
