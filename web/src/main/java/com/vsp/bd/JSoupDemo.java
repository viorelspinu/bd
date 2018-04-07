package com.vsp.bd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.vsp.bd.domain.Recipe;
import com.vsp.bd.service.StringHelper;
import com.vsp.bd.service.parser.website.WP;

public class JSoupDemo {

	private static void testParse() throws IOException {
		// https://www.e-retete.ro/retete/prajitura-cu-mere
		WP wp = new WP("lauraadamache.ro").title(".entry-title").body(".entry-content")
				.startHint("retete culinare laura adamache").endHint("voturi");
		Recipe recipe = wp
				.parseRecipe("https://www.lauraadamache.ro/2018/03/quesadilla-cu-ton-si-cascaval-afumat.html");
		System.out.println(recipe.getText());

	}

	private static void getText() throws IOException {
		String url = "http://www.costachel.ro/trufe-de-ciocolata-neagra/";
		Document doc = Jsoup.connect(url).timeout(60000)
				.userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
				.get();
		System.out.println(doc.select(".post").text());
	}

	public static void main(String[] args) throws IOException {
		getText();
		// testParse();
	}

	private static void parseDiversificareLinks() throws IOException {
		Set<String> urlSet = new HashSet<>();

		try {
			for (int i = 0; i < 300; i++) {
				String url = "https://diversificare.ro/author/cititor/page/" + (i + 1) + "/";
				// System.out.println(url);
				Document doc = Jsoup.connect(url).get();
				org.jsoup.select.Elements elements = doc.select("a");
				for (int j = 0; j < elements.size(); j++) {
					Element link = doc.select("a").get(j);
					String absHref = link.attr("abs:href"); // "http://jsoup.org/"
					if (!absHref.contains("retete/page")) {
						if (!absHref.contains("#comments")) {
							System.out.println(absHref);
							urlSet.add(absHref);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List list = new ArrayList(urlSet);
		Collections.sort(list);

		BufferedWriter out = new BufferedWriter(new FileWriter("links.txt"));
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			out.write(it.next());
			out.newLine();
		}
		out.close();
	}

	private static void parseDulceataDeTrandafiriLinks() throws IOException {
		Set<String> urlSet = new HashSet<>();

		try {
			for (int i = 0; i < 1; i++) {
				String url = "http://dulceatadetrandafiri.blogspot.ro/p/retete.html";
				// System.out.println(url);
				Document doc = Jsoup.connect(url).get();
				org.jsoup.select.Elements elements = doc.select("a");
				for (int j = 0; j < elements.size(); j++) {
					Element link = doc.select("a").get(j);
					String absHref = link.attr("abs:href"); // "http://jsoup.org/"
					if (!absHref.contains("retete/page")) {
						if (!absHref.contains("#comments")) {
							System.out.println(absHref);
							urlSet.add(absHref);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		List list = new ArrayList(urlSet);
		Collections.sort(list);

		BufferedWriter out = new BufferedWriter(new FileWriter("links.txt"));
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			out.write(it.next());
			out.newLine();
		}
		out.close();
	}

	private static void prepareLinkFile() throws FileNotFoundException, IOException {
		String file = "qw";
		String domain = "aromedemamica.ro";
		Set<String> lineSet = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				i++;

				if (line.contains("#")) {
					line = line.substring(0, line.indexOf("#"));
				}
				if (line.toLowerCase().contains(domain)) {
					lineSet.add(line);
				} else {
					System.out.println("NO DOMAIN");
					System.out.println(line);
				}
			}
		}

		System.out.println(lineSet.size());
		;
		List list = new ArrayList(lineSet);
		Collections.sort(list);

		BufferedWriter out = new BufferedWriter(new FileWriter("links-prepared.txt"));
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			out.write(it.next());
			out.newLine();
		}
		out.close();
	}

}
