package com.synopsys.integration.configuration.parse

/**
 * Splits the configuration provided value to a list of T around occurrences of the specified [delimiters].
 *
 * @param valueParser The ValueParser to be applied to each entry in the list.
 * @param delimiters One or more strings to be used as delimiters. Defaults to comma-separated.
 *
 * To avoid ambiguous results when strings in [delimiters] have characters in common, this method proceeds from
 * the beginning to the end of this string, and matches at each position the first element in [delimiters]
 * that is equal to a delimiter in this instance at that position.
 */
abstract class ListValueParser<T>(private val valueParser: ValueParser<T>, private vararg val delimiters: String = arrayOf(",")) : ValueParser<List<T>>() {
    override fun parse(value: String): List<T> {
        return value.split(*delimiters).map { valueParser.parse(it) }.toList()
    }
}