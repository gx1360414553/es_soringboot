package com.es.demo;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SpringDataEsTest {

//    @Autowired
//    private ElasticsearchTemplate elasticsearchTemplate;
//
//    @Test
//    public void highLightQueryTest() {
//        String field = "content";
//        String searchMessage = "三";
//        List<Poem> poems = highLigthQuery(field, searchMessage);
//        System.out.println(poems);
//    }
//
//    public List<Poem> highLigthQuery(String field, String searchMessage) {
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(QueryBuilders.termQuery(field, searchMessage))
//                .withHighlightFields(new HighlightBuilder.Field(field)).build();
//        Page<Poem> page = elasticsearchTemplate.queryForPage(searchQuery, Poem.class, new SearchResultMapper() {
//
//            @Override
//            public <T> Page<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
//                ArrayList<Poem> poems = new ArrayList<Poem>();
//                SearchHits hits = response.getHits();
//                for (SearchHit searchHit : hits) {
//                    if (hits.getHits().length <= 0) {
//                        return null;
//                    }
//                    Poem poem = new Poem();
//                    String highLightMessage = searchHit.getHighlightFields().get(field).fragments()[0].toString();
//                    poem.setId(Integer.parseInt(searchHit.getId()));
//                    poem.setTitle(String.valueOf(searchHit.getSource().get("title")));
//                    poem.setContent(String.valueOf(searchHit.getSource().get("content")));
//                    // 反射调用set方法将高亮内容设置进去
//                    try {
//                        String setMethodName = parSetName(field);
//                        Class<? extends Poem> poemClazz = poem.getClass();
//                        Method setMethod = poemClazz.getMethod(setMethodName, String.class);
//                        setMethod.invoke(poem, highLightMessage);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    poems.add(poem);
//                }
//                if (poems.size() > 0) {
//                    return new PageImpl<T>((List<T>) poems);
//                }
//                return null;
//            }
//        });
//        List<Poem> poems = page.getContent();
//        return poems;
//    }
//
//    /**
//     * 拼接在某属性的 set方法
//     *
//     * @param fieldName
//     * @return String
//     */
//    private static String parSetName(String fieldName) {
//        if (null == fieldName || "".equals(fieldName)) {
//            return null;
//        }
//        int startIndex = 0;
//        if (fieldName.charAt(0) == '_')
//            startIndex = 1;
//        return "set" + fieldName.substring(startIndex, startIndex + 1).toUpperCase()
//                + fieldName.substring(startIndex + 1);
//    }
}