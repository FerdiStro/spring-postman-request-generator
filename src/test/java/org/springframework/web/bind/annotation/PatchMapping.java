package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for mapping HTTP {@code PATCH} requests onto specific handler
 * methods.
 *
 * <p>Specifically, {@code @PatchMapping} is a <em>composed annotation</em> that
 * acts as a shortcut for {@code @RequestMapping(method = RequestMethod.PATCH)}.
 *
 * <p><strong>NOTE:</strong> This annotation cannot be used in conjunction with
 * other {@code @RequestMapping} annotations that are declared on the same method.
 * If multiple {@code @RequestMapping} annotations are detected on the same method,
 * a warning will be logged, and only the first mapping will be used. This applies
 * to {@code @RequestMapping} as well as composed {@code @RequestMapping} annotations
 * such as {@code @GetMapping}, {@code @PostMapping}, etc.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see RequestMapping
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.PATCH)
public @interface PatchMapping {

	/**
	 * Alias for {@link RequestMapping#name}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String name() default "";

	/**
	 * Alias for {@link RequestMapping#value}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String[] value() default {};

	/**
	 * Alias for {@link RequestMapping#path}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String[] path() default {};

	/**
	 * Alias for {@link RequestMapping#params}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String[] params() default {};

	/**
	 * Alias for {@link RequestMapping#headers}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String[] headers() default {};

	/**
	 * Alias for {@link RequestMapping#consumes}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String[] consumes() default {};

	/**
	 * Alias for {@link RequestMapping#produces}.
	 */
	//@AliasFor(annotation = RequestMapping.class)
	String[] produces() default {};

}
