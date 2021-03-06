package miner.parse;
import miner.parse.data.DataItem;
import miner.parse.data.Packer;
import miner.parse.util.HtmlUtil;
import miner.parse.util.JsonUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.*;
public class DocObject {
	private String document;
	private DocType doc_type;
	/* 字段存放处 */
	public Map<String, Element> html_map;
	public Map<String, String> json_map;
	/* 工具类声明 */
	private HtmlUtil h_util;
	private JsonUtil j_util;
	public DocType get_type() {
		return this.doc_type;
	}
	public Map<String, String> get_json_map() {
		return this.json_map;
	}
	public Map<String, Element> get_html_map() {
		return this.html_map;
	}

    //根据节点内容返回路径
	public String search(String content){
		if(doc_type.equals(DocType.HTML)){
			for(Map.Entry<String,Element> e:html_map.entrySet()){
				if(e.getValue().text().equals(content)){
					return e.getKey();
				}
				Attributes a=e.getValue().attributes();
				Iterator<Attribute> it=a.iterator();
				while (it.hasNext()){
					Attribute attr=it.next();
					String key=attr.getKey();
					String value=attr.getValue();
					if(value.equals(content)){
						return e.getKey()+"."+key;
					}
				}
			}
		} else if(doc_type.equals(DocType.JSON)||doc_type.equals(DocType.JSONP)){
			for(Map.Entry<String,String> e:json_map.entrySet()){
				if(e.getValue().equals(content)){
					return e.getKey();
				}
			}
		} else if(doc_type.equals(DocType.TEXT)){
			//to be added...
		}else if(doc_type.equals(DocType.XML)){
			//to be added...
		}
		return "no path found...";
	}
	public DocObject(String document, DocType doc_type) {
		if (doc_type.equals(DocType.JSONP)) {
			this.document = document.substring(document.indexOf('{'),
					document.length() - 1);
			if(this.document.endsWith(")")){
				this.document = this.document.substring(this.document.indexOf('{'),
						this.document.length() - 1);
			}
		} else {
			this.document = document;
		}
		this.doc_type = doc_type;
		System.out.println(this.document);
		this.html_map = new HashMap<String, Element>();
		this.json_map = new HashMap<String, String>();
	}
	public DocObject(String path, CharSet char_set) {
		String encoding = "UTF8";
		String tail_str = path.split("\\.")[1];
		if (tail_str.equals("js")) {
			doc_type = DocType.JSON;
		} else if (tail_str.equals("html") || tail_str.equals("htm")) {
			doc_type = DocType.HTML;
		} else if (tail_str.equals("xml")) {
			doc_type = DocType.XML;
		} else {
			doc_type = null;
		}
		if (char_set.equals(CharSet.UTF8)) {
			encoding = "UTF8";
		} else if (char_set.equals(CharSet.GBK)) {
			encoding = "GBK";
		} else if (char_set.equals(CharSet.GB2312)) {
			encoding = "GB2312";
		}
		this.document = get_doc_str(path, encoding);
		this.html_map = new HashMap<String, Element>();
		this.json_map = new HashMap<String, String>();
	}
	/* 得到文本字符串 */
	private String get_doc_str(String doc_path, String encoding) {
		File file = new File(doc_path);
		String doc_str = "";
		if (file.isFile() && file.exists()) {
			InputStreamReader read;
			try {
				read = new InputStreamReader(new FileInputStream(file),
						encoding);
				BufferedReader buffered_reader = new BufferedReader(read);
				String line = null;
				while ((line = buffered_reader.readLine()) != null) {
					doc_str += line;
				}
				buffered_reader.close();
				read.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return doc_str;
	}
	public void parse() {
		if (doc_type.equals(DocType.HTML)) {
			parse_html();
		} else if (doc_type.equals(DocType.JSON)) {
			parse_json();
		} else if (doc_type.equals(DocType.JSONP)){
			parse_json();
		}
	}
	public void parse_json() {
		j_util = new JsonUtil(this.document, this);
		j_util.parse();
	}
	public void parse_html() {
		h_util = new HtmlUtil(this.document, this);
		h_util.parse();
	}
	public String get_value(String path, String tag) {
		if (doc_type.equals(DocType.HTML)) {
			Element e = html_map.get(path);
			if(e==null){
				return "none";
			}else{
				String result;
				if (tag.equals("text")) {
					result = e.text();
				} else {
					result = e.attr(tag);
				}
				return result;
			}
		} else if (doc_type.equals(DocType.JSON)
				|| doc_type.equals(DocType.JSONP)) {
			String result="none";
			if(json_map.get(path)!=null){
				result=json_map.get(path);
			}
			return result;
		}
		return "none";
	}
	public static void testParse(String doc_str) {
		long start = System.currentTimeMillis();
        /* 抽取单个doc数据的规则库，多个set组成map */
		Map<String, RuleItem> data_rule_map = new HashMap<String, RuleItem>();
		data_rule_map.put("id_name", new RuleItem("name_name",
				"html0.body0.div8.div0.div0.div1.ul0.li_1_9_.dl0.dt0.a0.title"));
		data_rule_map.put("id_phone", new RuleItem("name_phone",
				"html0.body0.div8.div0.div0.div1.ul0.li_1_9_.dl0.dd0.em0.b0.text"));
		data_rule_map.put("id_address", new RuleItem("name_address",
				"html0.body0.div8.div0.div0.div1.ul0.li_1_9_.dl0.dd1.span1.text"));
		data_rule_map.put("id_sale", new RuleItem("name_sale",
				"html0.body0.div8.div0.div0.div1.ul0.li_1_9_.dl0.dd2.a0.text"));
		data_rule_map.put("id_page_link", new RuleItem("name_page_link",
				"html0.body0.div8.div0.div0.div1.div0.a_1_6_.href"));
        /* 封装数据的规则库map */
		Set<DataItem> data_item_set = new HashSet<DataItem>();
//		data_item_set.add(new DataItem("1", "1", "1", "1", "none", "none",
//				"none", "none", "id_name","id_phone","id_address","id_sale"));
		data_item_set.add(new DataItem("1", "1", "1", "1", "none", "none",
				"none", "none", "id_page_link"));
        /* 数据生成器 */
		Generator g = new Generator();
		g.create_obj(doc_str);
		for (Map.Entry<String, RuleItem> entry : data_rule_map.entrySet()) {
			g.set_rule(entry.getValue());
		}
		g.generate_data();
//		System.out.println(g.get_doc_obj().search("010-84916660"));
		Map<String, Object> m = g.get_result();// m里封装了所有抽取的数据
		Iterator<DataItem> data_item_it = data_item_set.iterator();
		while (data_item_it.hasNext()) {
			Packer packer = new Packer(data_item_it.next(), m, data_rule_map);
			String[] result_str=packer.pack();
			for(int i=0;i<result_str.length;i++){
				System.out.println(result_str[i]);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("time:"+(double)(end-start)/1000);
	}
	public String get_test_doc_str(String file_path){
		File file = new File("/Users/white/Desktop/workspace/test.html");
		String doc_str = "";
		if (file.isFile() && file.exists()) {
			InputStreamReader read;
			try {
				read = new InputStreamReader(new FileInputStream(file),
						"UTF8");
				BufferedReader buffered_reader = new BufferedReader(read);
				String line = null;
				while ((line = buffered_reader.readLine()) != null) {
					doc_str += line;
				}
				buffered_reader.close();
				read.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//        System.out.println(doc_str);
		return doc_str;
	}
	public void find_strs(String... strs){
		for(int i=0;i<strs.length;i++){
			System.out.println(this.search(strs[i]));
		}
	}
	public static void main(String[] args) {
		String doc_str=null;
		try {
			Document doc = Jsoup.connect("http://dealer.xcar.com.cn/d1_475/?type=1&page=1").get();
			doc_str=doc.toString();
			doc_str="{\"success\":true,\"message\":null,\"value\":20}";
		}catch (IOException e){
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
        /* 抽取单个doc数据的规则库，多个set组成map */
		Map<String, RuleItem> data_rule_map = new HashMap<String, RuleItem>();
		data_rule_map.put("id_success", new RuleItem("name_success",
				"success0"));
		data_rule_map.put("id_message", new RuleItem("name_message",
				"message0"));
		data_rule_map.put("id_value", new RuleItem("name_value",
				"value0"));
        /* 封装数据的规则库map */
		Set<DataItem> data_item_set = new HashSet<DataItem>();
		data_item_set.add(new DataItem("1", "1", "1", "1", "none", "none",
				"none", "none","id_success"));
		data_item_set.add(new DataItem("1", "1", "1", "1", "none", "none",
				"none", "none","id_message"));
		data_item_set.add(new DataItem("1", "1", "1", "1", "none", "none",
				"none", "none","id_value"));
        /* 数据生成器 */
		Generator g = new Generator();
		g.create_obj(doc_str);
		for (Map.Entry<String, RuleItem> entry : data_rule_map.entrySet()) {
			g.set_rule(entry.getValue());
		}
		g.generate_data();
		Map<String, Object> m = g.get_result();// m里封装了所有抽取的数据
		Iterator<DataItem> data_item_it = data_item_set.iterator();
		while (data_item_it.hasNext()) {
			Packer packer = new Packer(data_item_it.next(), m, data_rule_map);
			String[] result_str=packer.pack();
			for(int i=0;i<result_str.length;i++){
				System.out.println(result_str[i]);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("time:"+(double)(end-start)/1000);
	}
}