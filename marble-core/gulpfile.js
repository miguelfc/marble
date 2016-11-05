var gulp = require('gulp'),
    usemin = require('gulp-usemin'),
    wrap = require('gulp-wrap'),
    connect = require('gulp-connect'),
    watch = require('gulp-watch'),
    cssnano = require('gulp-cssnano'),
    minifyJs = require('gulp-uglify'),
    concat = require('gulp-concat'),
    less = require('gulp-less'),
    rename = require('gulp-rename'),
    htmlmin = require('gulp-htmlmin');

// TODO: Move the components folder to a temporary one
var paths = {
    scripts: 'src/main/resources/web/js/**/*.*',
    styles: 'src/main/resources/web/less/**/*.*',
    images: 'src/main/resources/web/img/*.*',
    templates: 'src/main/resources/web/templates/**/*.html',
    index: 'src/main/resources/web/index.html',
    fontawesome_fonts: 'src/main/resources/web/components/font-awesome/**/*.{ttf,woff,woff2,eof}',
    bootstrap_fonts: 'src/main/resources/web/components/bootstrap/**/*.{ttf,woff,woff2,eof}',
    ui_grid_fonts: 'src/main/resources/web/components/angular-ui-grid/**/*.{ttf,woff,woff2,eof,svg}',
    material_design_fonts: 'src/main/resources/web/components/material-design-icons/iconfont/*.{ttf,woff,woff2,eof,svg}',
    opensans_fonts: 'src/main/resources/web/components/open-sans-fontface/**/*.{ttf,woff,woff2,eof,svg}',
    bootstrap_less: 'src/main/resources/web/components/bootstrap/less/bootstrap.less'
};

/**
 * Handle bower components from index
 */
gulp.task('usemin', ['build-custom'], function() {
    return gulp.src(paths.index)
        .pipe(usemin({
            //js: [minifyJs(), 'concat'],
            css: [cssnano({safe: true, discardComments: {removeAll: false}}), 'concat'],
        }))
        .pipe(gulp.dest('src/main/resources/static/'));
});

/**
 * Copy assets
 */
gulp.task('build-assets', ['copy-fontawesome_fonts', 'copy-bootstrap_fonts', 'copy-ui_grid_fonts', 'copy-material_design_fonts', 'copy-opensans_fonts']);

gulp.task('copy-fontawesome_fonts', function() {
    return gulp.src(paths.fontawesome_fonts)
        .pipe(rename({
            dirname: '/fonts'
        }))
        .pipe(gulp.dest('src/main/resources/static/lib'));
});

gulp.task('copy-bootstrap_fonts', function() {
    return gulp.src(paths.bootstrap_fonts)
        .pipe(rename({
            dirname: '/fonts'
        }))
        .pipe(gulp.dest('src/main/resources/static/lib'));
});

gulp.task('copy-ui_grid_fonts', function() {
    return gulp.src(paths.ui_grid_fonts)
        .pipe(gulp.dest('src/main/resources/static/lib/css'));
});

gulp.task('copy-material_design_fonts', function() {
    return gulp.src(paths.material_design_fonts)
        .pipe(gulp.dest('src/main/resources/static/lib/fonts'));
});

gulp.task('copy-opensans_fonts', function() {
    return gulp.src(paths.opensans_fonts)
        .pipe(gulp.dest('src/main/resources/static/lib/css'));
});

/**
 * Handle custom files
 */
gulp.task('build-custom', ['custom-images', 'custom-js', 'custom-less', 'custom-templates']);

gulp.task('custom-images', function() {
    return gulp.src(paths.images)
        .pipe(gulp.dest('src/main/resources/static/img'));
});

gulp.task('custom-js', function() {
    return gulp.src(paths.scripts)
        //.pipe(minifyJs())
        .pipe(concat('dashboard.min.js'))
        .pipe(gulp.dest('src/main/resources/static/js'));
});

gulp.task('custom-less', function() {
    return gulp.src(paths.styles)
        .pipe(less())
        .pipe(gulp.dest('src/main/resources/static/css'));
});

gulp.task('custom-templates', function() {
    return gulp.src(paths.templates)
        .pipe(htmlmin())
        .pipe(gulp.dest('src/main/resources/static/templates'));
});

gulp.task('custom-bootstrap', function() {
    return gulp.src(paths.bootstrap_less)
        .pipe(less())
        .pipe(gulp.dest('src/main/resources/static/css'));
});

/**
 * Watch custom files
 */
gulp.task('watch', function() {
    gulp.watch([paths.images], ['custom-images']);
    gulp.watch([paths.styles], ['custom-less']);
    gulp.watch([paths.scripts], ['custom-js']);
    gulp.watch([paths.templates], ['custom-templates']);
    gulp.watch([paths.index], ['usemin']);
});

/**
 * Live reload server
 */
gulp.task('webserver', function() {
    connect.server({
        root: 'dist',
        livereload: true,
        port: 8888
    });
});

gulp.task('livereload', function() {
    gulp.src(['src/main/resources/static/**/*.*'])
        .pipe(watch())
        .pipe(connect.reload());
});

/**
 * Gulp tasks
 */
gulp.task('build', ['build-custom', 'build-assets', 'usemin']);

// Modified to allow execution from maven using mvn frontend:gulp
//gulp.task('default', ['build', 'webserver', 'livereload', 'watch']);

gulp.task('default', ['build']);
