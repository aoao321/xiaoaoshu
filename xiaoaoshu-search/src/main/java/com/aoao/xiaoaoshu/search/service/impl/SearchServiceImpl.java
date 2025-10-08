package com.aoao.xiaoaoshu.search.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aoao.framework.common.result.PageResult;
import com.aoao.xiaoaoshu.search.index.NoteIndex;
import com.aoao.xiaoaoshu.search.index.UserIndex;
import com.aoao.xiaoaoshu.search.model.vo.req.SearchNoteReqVO;
import com.aoao.xiaoaoshu.search.model.vo.req.SearchUserReqVO;
import com.aoao.xiaoaoshu.search.model.vo.rsp.SearchNoteRspVO;
import com.aoao.xiaoaoshu.search.model.vo.rsp.SearchUserRspVO;
import com.aoao.xiaoaoshu.search.service.SearchService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FieldValueFactorFunctionBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author aoao
 * @create 2025-10-08-21:38
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public PageResult<SearchUserRspVO> searchUser(SearchUserReqVO searchUserReqVO) {
        // 关键词
        String keyword = searchUserReqVO.getKeyword();
        // 第几页
        Integer pageNo = searchUserReqVO.getPageNo();
        // 定义10条一页
        int limit = 10;
        // 计算页偏移量
        int offset = (pageNo-1) * limit;

        // 构建 SearchRequest，指定索引(数据库)
        SearchRequest searchRequest = new SearchRequest(UserIndex.NAME);
        // 构建查询内容
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构建 multi_match 查询，查询 nickname 和 xiaohashu_id 字段
        sourceBuilder.query(QueryBuilders.multiMatchQuery(
                keyword, UserIndex.FIELD_USER_NICKNAME, UserIndex.FIELD_USER_XIAOAOSHU_ID));
        // 排序，按 fans_total 降序
        SortBuilder<?> sortBuilder = new FieldSortBuilder(UserIndex.FIELD_USER_FANS_TOTAL)
                .order(SortOrder.DESC);
        sourceBuilder.sort(sortBuilder);
        // 分页
        sourceBuilder.from(offset);
        sourceBuilder.size(limit);
        // 设置高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(UserIndex.FIELD_USER_NICKNAME)
                .preTags("<strong>") // 设置包裹标签
                .postTags("</strong>");
        sourceBuilder.highlighter(highlightBuilder);
        // 将构建的查询条件设置到 SearchRequest 中
        searchRequest.source(sourceBuilder);
        // 返参 VO 集合
        List<SearchUserRspVO> searchUserRspVOS = null;
        // 总文档数，默认为 0
        long total = 0;
        try {
            // 执行查询请求
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 处理搜索结果
            total = searchResponse.getHits().getTotalHits().value;
            log.info("==> 命中文档总数, hits: {}", total);
            searchUserRspVOS = Lists.newArrayList();
            // 获取搜索命中的文档列表
            SearchHits hits = searchResponse.getHits();
            for (SearchHit hit : hits) {
                log.info("==> 文档数据: {}", hit.getSourceAsString());
                // 获取高亮字段
                String highlightedNickname = null;
                if (CollUtil.isNotEmpty(hit.getHighlightFields())
                        && hit.getHighlightFields().containsKey(UserIndex.FIELD_USER_NICKNAME)) {
                    highlightedNickname = hit.getHighlightFields().get(UserIndex.FIELD_USER_NICKNAME).fragments()[0].string();
                }
                // 获取文档的所有字段（以 Map 的形式返回）
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                // 提取特定字段值
                Long userId = ((Number) sourceAsMap.get(UserIndex.FIELD_USER_ID)).longValue();
                String nickname = (String) sourceAsMap.get(UserIndex.FIELD_USER_NICKNAME);
                String avatar = (String) sourceAsMap.get(UserIndex.FIELD_USER_AVATAR);
                String xiaoaoshuId = (String) sourceAsMap.get(UserIndex.FIELD_USER_XIAOAOSHU_ID);
                Integer noteTotal = (Integer) sourceAsMap.get(UserIndex.FIELD_USER_NOTE_TOTAL);
                Integer fansTotal = (Integer) sourceAsMap.get(UserIndex.FIELD_USER_FANS_TOTAL);

                // 构建 VO 实体类
                SearchUserRspVO searchUserRspVO = SearchUserRspVO.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .avatar(avatar)
                        .xiaoaoshuId(xiaoaoshuId)
                        .noteTotal(noteTotal)
                        .fansTotal(fansTotal)
                        .highlightNickname(highlightedNickname)
                        .build();
                searchUserRspVOS.add(searchUserRspVO);
            }
        } catch (IOException e) {
            log.error("==> 查询 Elasticserach 异常: ", e);
        }
        return PageResult.success(searchUserRspVOS, pageNo, total, limit);
    }

    @Override
    public PageResult<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO) {
        // 标题关键字
        String keyword = searchNoteReqVO.getKeyword();
        // 当前页数
        Integer pageNo = searchNoteReqVO.getPageNo();
        // 页大小
        int limit = 10;
        // 偏移量
        int offset = (pageNo-1) * limit;

        // 构建 SearchRequest，指定要查询的索引
        SearchRequest searchRequest = new SearchRequest(NoteIndex.NAME);
        // 创建查询构建器
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 创建查询条件
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field(NoteIndex.FIELD_NOTE_TITLE, 2.0f) // 手动设置笔记标题的权重值为 2.0
                .field(NoteIndex.FIELD_NOTE_TOPIC) // 不设置，权重默认为 1.0
                ;
        // 指定function计算score
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[] {
                // function 1
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        new FieldValueFactorFunctionBuilder(NoteIndex.FIELD_NOTE_LIKE_TOTAL)
                                .factor(0.5f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(0)
                ),
                // function 2
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        new FieldValueFactorFunctionBuilder(NoteIndex.FIELD_NOTE_COLLECT_TOTAL)
                                .factor(0.3f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(0)
                ),
                // function 3
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        new FieldValueFactorFunctionBuilder(NoteIndex.FIELD_NOTE_COMMENT_TOTAL)
                                .factor(0.2f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(0)
                )
        };
        // 构建 function_score 查询
        // "score_mode": "sum",
        // "boost_mode": "sum"
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder,
                        filterFunctionBuilders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM) // score_mode 为 sum
                .boostMode(CombineFunction.SUM); // boost_mode 为 sum
        // 设置查询
        sourceBuilder.query(functionScoreQueryBuilder);
        // 设置排序
        FieldSortBuilder sortBuilder = new FieldSortBuilder("_score").order(SortOrder.DESC);
        sourceBuilder.sort(sortBuilder); // 按照 _score 降序
        // 分页
        sourceBuilder.from(offset);
        sourceBuilder.size(limit);
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(NoteIndex.FIELD_NOTE_TITLE)
                .preTags("<strong>") // 设置包裹标签
                .postTags("</strong>");
        sourceBuilder.highlighter(highlightBuilder);
        // 加入request
        searchRequest.source(sourceBuilder);
        // 返回vo
        List<SearchNoteRspVO> searchNoteRspVOS = null;
        long total = 0;
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 查询出来的总条数
            total = response.getHits().getTotalHits().value;
            searchNoteRspVOS = Lists.newArrayList();
            // 处理结果
            SearchHits hits = response.getHits();
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();

                // 提取特定字段值
                Long noteId = (Long) sourceAsMap.get(NoteIndex.FIELD_NOTE_ID);
                String cover = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_COVER);
                String title = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_TITLE);
                String avatar = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_AVATAR);
                String nickname = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_NICKNAME);
                // 获取更新时间
                String updateTimeStr = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_UPDATE_TIME);
                LocalDateTime updateTime = LocalDateTime.parse(updateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                Integer likeTotal = (Integer) sourceAsMap.get(NoteIndex.FIELD_NOTE_LIKE_TOTAL);

                // 高亮字段
                String highlightedTitle = null;
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if (CollUtil.isNotEmpty(highlightFields)
                        && hit.getHighlightFields().containsKey(NoteIndex.FIELD_NOTE_TITLE)) {
                    highlightedTitle = hit.getHighlightFields().get(NoteIndex.FIELD_NOTE_TITLE).fragments()[0].string();
                }

                // 构建 VO 实体类
                SearchNoteRspVO searchNoteRspVO = SearchNoteRspVO.builder()
                        .noteId(noteId)
                        .cover(cover)
                        .title(title)
                        .highlightTitle(highlightedTitle)
                        .avatar(avatar)
                        .nickname(nickname)
                        .updateTime(updateTime)
                        .likeTotal(likeTotal)
                        .build();
                searchNoteRspVOS.add(searchNoteRspVO);

            }
        } catch (IOException e) {
            log.error("==> 查询 Elasticserach 异常: ", e);
        }
        return PageResult.success(searchNoteRspVOS,pageNo,total);
    }
}
