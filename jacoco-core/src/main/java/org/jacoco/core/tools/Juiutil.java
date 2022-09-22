package org.jacoco.core.tools;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Juiutil {

    private static  HashMap<String,String> juimap = new LinkedHashMap<>();

    private String  createjui(List<String> param){
        return null;
    }

    private static void initmap(){
        juimap.put("boolean","Z");
        juimap.put("byte","B");
        juimap.put("char","C");
        juimap.put("short","S");
        juimap.put("int","I");
        juimap.put("long","J");
        juimap.put("double","D");
        juimap.put("float","F");
        juimap.put("void","V");
        juimap.put("boolean[]","[Z");
        juimap.put("char[]","[C");
        juimap.put("byte[]","[B");
        juimap.put("short[]","[S");
        juimap.put("int[]","[I");
        juimap.put("long[]","[J");
        juimap.put("double[]","[D");
        juimap.put("float[]","[F");
        juimap.put("void[]","[V");
        juimap.put("boolean[][]","[[Z");
        juimap.put("char[][]","[[C");
        juimap.put("byte[][]","[[B");
        juimap.put("short[][]","[[S");
        juimap.put("int[][]","[[I");
        juimap.put("long[][]","[[J");
        juimap.put("double[][]","[[D");
        juimap.put("float[][]","[[F");
        juimap.put("void[][]","[[V");
    }

//    public static  void   jacocodesc(String str){
//        List<String> list = Juiutil.demo(str);
//        for(String l:list){
//            System.out.println(l);
//        }
//    }


    public  static  String  JacocodescTran(String str){
        String[] arr = str.split(";");
        List<String> list = new LinkedList<>();
        for(String s: arr){
            List<String> res = new LinkedList<>();
            res = checkobjuct(s);
            for(String l:res){
              list.add(l);
            }
        }
        List<String> res = new LinkedList<>();
        String juires = "";
       for(String k : list){
           String[] jui = k.split("/");
           int i = jui.length;
           if(i>1){
               juires+=jui[i-1];
           }else{
               juires+=k;
           }
       }
       return juires;
    }

    public static String diffadminTran(List<String> parameters){
        if(parameters.size()==0){
            return "()";
        }else{
            String res = "(";
            for(String k : parameters){
                Juiutil.initmap();
                if(juimap.containsKey(k)){
                    String value = juimap.get(k);
                    res+=value;
                }else{
                    char[] check = k.toCharArray();
                    int flag = 0;
                    String cond = "";
                    for(char d: check){
                        if(d=='<'||d=='['){
                            cond = k.substring(0,flag);
                            break;
                        }else{
                            flag+=1;

                        }
                    }
                    if(cond.equals("")){
                        cond = k;
                    }
                    res+=cond;
                }
            }
            return res+")V";
        }
    }


    public static   List<String>  checkobjuct(String str){
        int node = 0;
        int node1 = 1;
        char[] chars = str.toCharArray();
        List<String> list = new LinkedList<>();
        if(chars[0]=='L'){
          list.add(str);
          return list;
        }
        while (node<node1 && node1<chars.length){
            if(chars[node1]=='L' && chars[node1-1]!='/' && chars[node1-1]!='['){
                list.add(str.substring(node,node1));
                node = node1;
            }else if(node+1<chars.length && chars[node]=='[' && chars[node1]!='[' && chars[node1+1]!='L'){
              list.add(str.substring(node,node1+1));
              node = node1+1;
              list.add(str.substring(node,chars.length));
            }
            node1+=1;
        }
        if(node!=node1){
            list.add(str.substring(node,node1));
        }

        return list;
    }



    public static void main(String[] args) {
        String str = "(Ljava/lang/Object;)Z";
        String r = "Ljava/lang/String";
        System.out.println(Juiutil.JacocodescTran(str));
    }

}
