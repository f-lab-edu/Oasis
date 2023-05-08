package com.flab.oasis.constant;

public enum BookCategory {
    BC101("소설"),
    BC102("시/에세이"),
    BC103("예술/대중문화"),
    BC104("사회과학"),
    BC105("역사와 문화"),
    BC108("만화"),
    BC111("가정과 생활"),
    BC116("자연과 과학"),
    BC119("인문"),
    BC120("종교/역학"),
    BC124("취미/레저");

    private final String name;

    BookCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
