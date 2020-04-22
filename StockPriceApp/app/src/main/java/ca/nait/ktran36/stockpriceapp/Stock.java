package ca.nait.ktran36.stockpriceapp;

public class Stock
{
    private String symbol;
    private String companyName;
    private int open;
    private int close;

    public Stock() {
    }

    public String getSymbol() {
        return symbol;
    }

    public Stock(String symbol, String companyName, int open, int close) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.open = open;
        this.close = close;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }
}
