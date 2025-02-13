package com.javaacademy.cinema.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieDto {
    @Schema(description = "Название фильма", defaultValue = "«V» значит Вендетта")
    private String name;
    @Schema(description = "Описание фильма", defaultValue = "Альтернативное будущее. В Англии после страшной эпидемии "
            + "неизвестного вируса повергшей страну в хаос, устанавливается жестокая диктатура властного канцлера "
            + "со всеми сопутствующими прелестями: комендантский час, всевластие членов партии над простыми людьми "
            + "и, конечно, сотни тайных стукачей-осведомителей. Однажды ночью в Лондоне появляется борец за свободу, "
            + "известный как V, который начинает партизанскую войну с режимом в попытке вернуть народу "
            + "отнятую у него свободу. В этой войне он привлекает на свою сторону молодую женщину, "
            + "которую он вырвал из лап тайной полиции.")
    private String description;
}
