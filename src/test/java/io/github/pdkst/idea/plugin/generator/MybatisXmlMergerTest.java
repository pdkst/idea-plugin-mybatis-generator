package io.github.pdkst.idea.plugin.generator;

import io.github.pdkst.idea.plugin.common.pojo.MybatisXml;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author pdkst
 * @since 2025/03/08
 */
class MybatisXmlMergerTest {

    @Test
    void parse() {
        final String sourceXml = "";
        final String targetXml = "";
        MybatisXml mybatisXml = MybatisXmlMerger.parse(sourceXml);
        assertNotNull(mybatisXml);
        MybatisXmlMerger.merge(targetXml, mybatisXml);
    }
}