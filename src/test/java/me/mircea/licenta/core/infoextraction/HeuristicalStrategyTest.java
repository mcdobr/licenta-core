package me.mircea.licenta.core.infoextraction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import me.mircea.licenta.core.entities.Book;
import me.mircea.licenta.core.entities.WebWrapper;
import me.mircea.licenta.core.utils.HtmlUtil;

public class HeuristicalStrategyTest {
	InformationExtractionStrategy extractionStrategy = new HeuristicalStrategy();

	@Test
	public void shouldExtractElementsFromDownloadedMultiPage() throws IOException {
		final ClassLoader classLoader = getClass().getClassLoader();
		final URL resource = classLoader.getResource("extractionTestInput.html");
		assertNotNull(resource);

		File inputFile = new File(resource.getFile());
		assertTrue(inputFile.exists());

		Document doc = Jsoup.parse(inputFile, "UTF-8", "http://www.librariilealexandria.ro/carte");

		Elements productElements = extractionStrategy.extractBookCards(doc);
		assertNotNull(productElements);
		assertTrue(2000 <= productElements.size());
	}

	@Test
	public void shouldExtractAttributes() throws IOException {
		Document doc = HtmlUtil.sanitizeHtml(
				Jsoup.connect("https://carturesti.ro/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1995").get());

		String elementStr = "<prod-grid-box class=\"product-grid-container\" itemscope=\"\" itemtype=\"http://schema.org/Book\"><a class=\"clean-a\" data-ng-href=\"/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1\" title=\"Trecute vieti de doamne si domnite\" data-ng-click=\"onProductClick($event, product,key +1)\" href=\"/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1\">"
				+ "<!-- ngIf: ::product.imgUrl --><div data-ng-if=\"::product.imgUrl\" class=\"productImageContainer ng-scope\" style=\"\">"
				+ "<img data-ng-src=\"//cdn.cartu.ro/img/prod/240/82699986-0-240.jpeg\" itemprop=\"image\" src=\"//cdn.cartu.ro/img/prod/240/82699986-0-240.jpeg\">"
				+ "</div><!-- end ngIf: ::product.imgUrl -->" + "" + "</a>"
				+ "<div class=\"grid-product-details layout-align-center-center layout-column\" layout=\"column\" layout-align=\"center center\">"
				+ "<a data-ng-href=\"/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1\" itemprop=\"url\" content=\"/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1\" data-ng-click=\"onProductClick($event, product, key +1)\" href=\"/carte/trecute-vieti-de-doamne-si-domnite-82699986?p=1\">"
				+ "<h5 class=\"md-title ng-binding\" data-ng-bind=\"::product.name\" itemprop=\"name\">Trecute vieti de doamne si domnite</h5>"
				+ "</a>"
				+ "<div class=\"subtitlu-produs ng-binding\" data-ng-bind-html=\"::h.htmlDecode(h.parseSubtitle(product.subtitle))\"><a href=\"/autor/constantin_gane\">Constantin Gane</a></div>"
				+ "</div>" + "<!-- ngIf: ::product.nou -->" + "<!-- ngIf: ::product.discount -->"
				+ "<!-- ngIf: ::product.specialPrice -->"
				+ "<div class=\"productStock in-stoc\" data-ng-class=\"::product.stockStatus.slug\" style=\"\"><span data-ng-bind=\"::product.stockStatus.label\" class=\"ng-binding\">În stoc</span><!-- ngIf: ::product.stockStatus['24h'] --><span class=\"stoc24h ng-scope\" data-ng-if=\"::product.stockStatus['24h']\" style=\"\"></span><!-- end ngIf: ::product.stockStatus['24h'] --></div>"
				+ "<!-- ngIf: ::product.stockStatus.slug != 'indisponibil' && product.stockStatus.slug != 'promo' --><div data-ng-if=\"::product.stockStatus.slug != 'indisponibil' &amp;&amp; product.stockStatus.slug != 'promo'\" data-ng-bind-html=\"::h.formatPrice(product.price)\" class=\"productPrice ng-binding ng-scope\" data-ng-class=\"::product.discount?'discountPrice':''\" style=\"\"><span class=\"suma\" itemprop=\"price\" content=\"69.00\">69</span><span class=\"bani\">00</span><span class=\"priceCurrency\" content=\"RON\">lei</span></div><!-- end ngIf: ::product.stockStatus.slug != 'indisponibil' && product.stockStatus.slug != 'promo' --></prod-grid-box>";

		Element htmlElement = Jsoup.parse(elementStr);

		Book book = extractionStrategy.extractBook(htmlElement, doc);
		assertNotNull(book.getIsbn());
		assertNotEquals(book.getIsbn().trim(), "");
	}

	@Test
	public void shouldCreateAppropriateWrapperOnCarturesti() throws IOException {
		String url = "https://carturesti.ro/carte/pedaland-prin-viata-181658144?p=2";
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect(url).get());

		String cartuHtml = "<div class=\"cartu-grid-list\" grid-resize=\"\">\r\n"
				+ "<!-- ngRepeat: (key,product) in gridProducts track by product.id --><div class=\"cartu-grid-tile ng-scope\" data-ng-repeat=\"(key,product) in gridProducts track by product.id\" data-ng-controller=\"dCatProd\" data-ng-style=\"getMinHeight($parent)\" style=\"min-height: 354.05px;\">\r\n"
				+ "<prod-grid-box class=\"product-grid-container\" itemscope=\"\" itemtype=\"http://schema.org/Product\"><a class=\"clean-a\" data-ng-href=\"/carte/pedaland-prin-viata-181658144?p=1\" title=\"Pedaland prin viata\" data-ng-click=\"onProductClick($event, product,key +1)\" href=\"/carte/pedaland-prin-viata-181658144?p=1\">\r\n"
				+ "\r\n"
				+ "<!-- ngIf: ::product.imgUrl --><div data-ng-if=\"::product.imgUrl\" class=\"productImageContainer ng-scope\">\r\n"
				+ "<img data-ng-src=\"https://cdn.dc5.ro/img/prod/240/181658144-0-240.jpeg\" itemprop=\"image\" src=\"https://cdn.dc5.ro/img/prod/240/181658144-0-240.jpeg\">\r\n"
				+ "</div><!-- end ngIf: ::product.imgUrl -->\r\n" + "\r\n" + "</a>\r\n"
				+ "<div class=\"grid-product-details layout-align-center-center layout-column\" layout=\"column\" layout-align=\"center center\">\r\n"
				+ "<a data-ng-href=\"/carte/pedaland-prin-viata-181658144?p=1\" itemprop=\"url\" content=\"/carte/pedaland-prin-viata-181658144?p=1\" data-ng-click=\"onProductClick($event, product, key +1)\" href=\"/carte/pedaland-prin-viata-181658144?p=1\">\r\n"
				+ " <h5 class=\"md-title ng-binding\" data-ng-bind=\"::product.name\" itemprop=\"name\">Pedaland prin viata</h5>\r\n"
				+ "</a>\r\n"
				+ "<div class=\"subtitlu-produs ng-binding\" data-ng-bind-html=\"::h.htmlDecode(h.parseSubtitle(product.subtitle))\"><a href=\"/autor/codrut_constantinescu\">Codrut Constantinescu</a></div>\r\n"
				+ "</div>\r\n" + "<!-- ngIf: ::product.nou -->\r\n" + "<!-- ngIf: ::product.discount -->\r\n"
				+ "<!-- ngIf: ::product.specialPrice -->\r\n"
				+ "<div class=\"productStock stoc-limitat\" data-ng-class=\"::product.stockStatus.slug\"><span data-ng-bind=\"::product.stockStatus.label\" class=\"ng-binding\">Limitat</span><!-- ngIf: ::product.stockStatus['24h'] --><span class=\"stoc24h ng-scope\" data-ng-if=\"::product.stockStatus['24h']\"></span><!-- end ngIf: ::product.stockStatus['24h'] --></div>\r\n"
				+ "<!-- ngIf: ::product.stockStatus.slug != 'indisponibil' && product.stockStatus.slug != 'promo' --><div data-ng-if=\"::product.stockStatus.slug != 'indisponibil' &amp;&amp; product.stockStatus.slug != 'promo'\" data-ng-bind-html=\"::h.formatPrice(product.price)\" class=\"productPrice ng-binding ng-scope\" data-ng-class=\"::product.discount?'discountPrice':''\"><span class=\"suma\" itemprop=\"price\" content=\"25.00\">25</span><span class=\"bani\">00</span><span class=\"priceCurrency\" content=\"RON\">lei</span></div><!-- end ngIf: ::product.stockStatus.slug != 'indisponibil' && product.stockStatus.slug != 'promo' --></prod-grid-box>\r\n"
				+ "</div><!-- end ngRepeat: (key,product) in gridProducts track by product.id -->\r\n"
				+ "</div>";
		Elements additionals = new Elements();
		additionals.add(Jsoup.parseBodyFragment(cartuHtml));

		WrapperGenerationStrategy strategy = new HeuristicalStrategy();
		WebWrapper wrapper = strategy.generateWrapper(mainContent, additionals);

		System.out.println(wrapper.toString());

		assertEquals(".titluProdus", wrapper.getTitleSelector());
		assertEquals(".autorProdus", wrapper.getAuthorsSelector());
		assertEquals(".pret", wrapper.getPriceSelector());
		assertEquals(".productAttr", wrapper.getAttributeSelector());
		assertEquals(".product-grid-container", wrapper.getBookCardSelector());
	}

	@Test
	public void shouldCreateAppropriateWrapperOnLibris() throws IOException {
		String url = "https://www.libris.ro/naufragii-akira-yoshimura-HUM978-606-779-038-2--p1033264.html";
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect(url).get());
		WrapperGenerationStrategy strategy = new HeuristicalStrategy();
		WebWrapper wrapper = strategy.generateWrapper(mainContent);

		System.out.println(wrapper.toString());
		assertEquals("#text_container>p", wrapper.getAttributeSelector());
	}

	@Test
	public void shouldCreateAppropriateWrapperOnAlexandria() throws IOException {
		String url = "http://www.librariilealexandria.ro/elita-din-umbra";
		Element mainContent = HtmlUtil.extractMainContent(Jsoup.connect(url).get());
		WrapperGenerationStrategy strategy = new HeuristicalStrategy();
		WebWrapper wrapper = strategy.generateWrapper(mainContent);

		System.out.println(wrapper.toString());

		assertEquals(".product-author", wrapper.getAuthorsSelector());
		// TODO: add more (exclude .price)
	}
}
